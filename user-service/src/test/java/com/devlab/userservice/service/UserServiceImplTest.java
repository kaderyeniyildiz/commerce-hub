package com.devlab.userservice.service;

import com.devlab.userservice.dto.UserDTO;
import com.devlab.userservice.entity.User;
import com.devlab.userservice.enums.UserStatus;
import com.devlab.userservice.exception.EmailAlreadyExistsException;
import com.devlab.userservice.exception.EmailNotFoundException;
import com.devlab.userservice.exception.InvalidCurrentPasswordException;
import com.devlab.userservice.exception.UserNotFoundException;
import com.devlab.userservice.kafka.producer.UserEventProducer;
import com.devlab.userservice.repository.UserRepository;
import com.devlab.userservice.request.ChangePasswordRequest;
import com.devlab.userservice.request.UserCreateRequest;
import com.devlab.userservice.request.UserCriteriaRequest;
import com.devlab.userservice.request.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final Long USER_ID = 42L;
    private static final String EMAIL = "ada@example.com";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void givenExistingUser_whenGetUserById_thenReturnsUserDto() {
        User user = new User();
        UserDTO expected = userDto(USER_ID, EMAIL);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(expected);

        UserDTO result = userService.getUserById(USER_ID);

        assertThat(result).isSameAs(expected);
        verify(userRepository).findById(USER_ID);
        verify(modelMapper).map(user, UserDTO.class);
    }

    @Test
    void givenMissingUser_whenGetUserById_thenThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(USER_ID);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void givenPageOfUsers_whenGetAllUsers_thenReturnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 2);
        User firstUser = new User();
        User secondUser = new User();
        UserDTO firstDto = userDto(1L, "ada@example.com");
        UserDTO secondDto = userDto(2L, "grace@example.com");
        Page<User> users = new PageImpl<>(List.of(firstUser, secondUser), pageable, 2);
        when(userRepository.findAll(pageable)).thenReturn(users);
        when(modelMapper.map(firstUser, UserDTO.class)).thenReturn(firstDto);
        when(modelMapper.map(secondUser, UserDTO.class)).thenReturn(secondDto);

        Page<UserDTO> result = userService.getAllUsers(pageable);

        assertThat(result.getContent()).containsExactly(firstDto, secondDto);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(userRepository).findAll(pageable);
    }

    @Test
    void givenAvailableEmail_whenCreateUser_thenEncodesPasswordSavesUserAndPublishesEvent() {
        UserCreateRequest request = createRequest(EMAIL);
        UserDTO expected = userDto(USER_ID, EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            UserCreateRequest source = invocation.getArgument(0);
            User target = invocation.getArgument(1);
            target.setFirstName(source.getFirstName());
            target.setLastName(source.getLastName());
            target.setEmail(source.getEmail());
            target.setPhoneNumber(source.getPhoneNumber());
            target.setStatus(source.getStatus());
            return null;
        }).when(modelMapper).map(same(request), any(User.class));
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        User persistedUser = mock(User.class);
        when(persistedUser.getId()).thenReturn(USER_ID);
        when(userRepository.save(any(User.class))).thenReturn(persistedUser);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(expected);

        UserDTO result = userService.createUser(request);

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertThat(result).isSameAs(expected);
        assertThat(savedUser.getValue().getFirstName()).isEqualTo("Ada");
        assertThat(savedUser.getValue().getEmail()).isEqualTo(EMAIL);
        assertThat(savedUser.getValue().getPassword()).isEqualTo("encoded-password");
        verify(passwordEncoder).encode("plain-password");
        verify(userEventProducer).sendUserCreatedEvent(USER_ID, EMAIL);
    }

    @Test
    void givenEmailAlreadyUsed_whenCreateUser_thenThrowsEmailAlreadyExistsException() {
        UserCreateRequest request = createRequest(EMAIL);
        User existingUser = mock(User.class);
        when(existingUser.getId()).thenReturn(99L);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder, modelMapper, userEventProducer);
    }

    @Test
    void givenExistingUserAndAvailableEmail_whenUpdateUser_thenSavesAndReturnsMappedDto() {
        UserUpdateRequest request = updateRequest("ada.updated@example.com");
        User existingUser = mock(User.class);
        UserDTO expected = userDto(USER_ID, "ada.updated@example.com");
        when(existingUser.getId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        doAnswer(invocation -> null).when(modelMapper).map(same(request), same(existingUser));
        when(modelMapper.map(existingUser, UserDTO.class)).thenReturn(expected);

        UserDTO result = userService.updateUser(USER_ID, request);

        assertThat(result).isSameAs(expected);
        verify(modelMapper).map(request, existingUser);
        verify(userRepository).save(existingUser);
        verify(userRepository).findByEmail(request.getEmail());
    }

    @Test
    void givenMissingUser_whenUpdateUser_thenThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(USER_ID, updateRequest(EMAIL)))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(modelMapper);
    }

    @Test
    void givenEmailBelongingToAnotherUser_whenUpdateUser_thenThrowsEmailAlreadyExistsException() {
        UserUpdateRequest request = updateRequest(EMAIL);
        User currentUser = mock(User.class);
        User otherUser = mock(User.class);
        when(currentUser.getId()).thenReturn(USER_ID);
        when(otherUser.getId()).thenReturn(99L);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> userService.updateUser(USER_ID, request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(modelMapper);
    }

    @Test
    void givenExistingUser_whenDeleteUser_thenDeletesUserAndPublishesEvent() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.deleteUser(USER_ID);

        verify(userRepository).delete(user);
        verify(userEventProducer).sendUserDeletedEvent(USER_ID);
    }

    @Test
    void givenMissingUser_whenDeleteUser_thenThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).delete(any(User.class));
        verifyNoInteractions(userEventProducer);
    }

    @Test
    void givenValidCurrentPassword_whenChangePassword_thenEncodesAndSavesNewPassword() {
        ChangePasswordRequest request = changePasswordRequest("old-password", "new-password");
        User user = mock(User.class);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn("encoded-old-password");
        when(passwordEncoder.matches("old-password", "encoded-old-password")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        userService.changePassword(USER_ID, request);

        verify(passwordEncoder).matches("old-password", "encoded-old-password");
        verify(passwordEncoder).encode("new-password");
        verify(user).setPassword("encoded-new-password");
        verify(userRepository).save(user);
    }

    @Test
    void givenInvalidCurrentPassword_whenChangePassword_thenThrowsAndDoesNotSave() {
        ChangePasswordRequest request = changePasswordRequest("wrong-password", "new-password");
        User user = mock(User.class);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn("encoded-old-password");
        when(passwordEncoder.matches("wrong-password", "encoded-old-password")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(USER_ID, request))
                .isInstanceOf(InvalidCurrentPasswordException.class);

        verify(passwordEncoder, never()).encode(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenMissingUser_whenChangePassword_thenThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(
                USER_ID, changePasswordRequest("old-password", "new-password")))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenExistingEmail_whenGetUserByEmail_thenReturnsUserDto() {
        User user = new User();
        UserDTO expected = userDto(USER_ID, EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(expected);

        UserDTO result = userService.getUserByEmail(EMAIL);

        assertThat(result).isSameAs(expected);
        verify(userRepository).findByEmail(EMAIL);
        verify(modelMapper).map(user, UserDTO.class);
    }

    @Test
    void givenMissingEmail_whenGetUserByEmail_thenThrowsEmailNotFoundException() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(EMAIL))
                .isInstanceOf(EmailNotFoundException.class);

        verifyNoInteractions(modelMapper);
    }

    @Test
    void givenSearchCriteriaAndResults_whenSearchUsers_thenReturnsMappedPage() {
        UserCriteriaRequest request = criteriaRequest();
        Pageable pageable = PageRequest.of(1, 5);
        User user = new User();
        UserDTO expected = userDto(USER_ID, EMAIL);
        Page<User> users = new PageImpl<>(List.of(user), pageable, 6);
        when(userRepository.searchUsersByCriteria(
                "Ada", "Lovelace", EMAIL, "+61123456789", UserStatus.ACTIVE, pageable))
                .thenReturn(users);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(expected);

        Page<UserDTO> result = userService.searchUsers(request, pageable);

        assertThat(result.getContent()).containsExactly(expected);
        assertThat(result.getTotalElements()).isEqualTo(6);
        verify(userRepository).searchUsersByCriteria(
                "Ada", "Lovelace", EMAIL, "+61123456789", UserStatus.ACTIVE, pageable);
    }

    private UserCreateRequest createRequest(String email) {
        UserCreateRequest request = new UserCreateRequest();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setEmail(email);
        request.setPassword("plain-password");
        request.setPhoneNumber("+61123456789");
        request.setStatus(UserStatus.ACTIVE);
        return request;
    }

    private UserUpdateRequest updateRequest(String email) {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setEmail(email);
        request.setPhoneNumber("+61123456789");
        request.setStatus(UserStatus.ACTIVE);
        return request;
    }

    private ChangePasswordRequest changePasswordRequest(String currentPassword, String newPassword) {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(currentPassword);
        request.setNewPassword(newPassword);
        return request;
    }

    private UserCriteriaRequest criteriaRequest() {
        UserCriteriaRequest request = new UserCriteriaRequest();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setEmail(EMAIL);
        request.setPhoneNumber("+61123456789");
        request.setStatus(UserStatus.ACTIVE);
        return request;
    }

    private UserDTO userDto(Long id, String email) {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setFirstName("Ada");
        dto.setLastName("Lovelace");
        dto.setEmail(email);
        dto.setStatus(UserStatus.ACTIVE);
        return dto;
    }
}
