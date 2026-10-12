package com.commercehub.user.service;

import com.commercehub.user.dto.UserDTO;
import com.commercehub.user.entity.User;
import com.commercehub.user.exception.EmailAlreadyExistsException;
import com.commercehub.user.exception.EmailNotFoundException;
import com.commercehub.user.exception.UserNotFoundException;
import com.commercehub.user.exception.InvalidCurrentPasswordException;
import com.commercehub.user.repository.UserRepository;
import com.commercehub.user.request.ChangePasswordRequest;
import com.commercehub.user.request.UserCreateRequest;
import com.commercehub.user.request.UserCriteriaRequest;
import com.commercehub.user.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new UserNotFoundException(id));
        return toDto(user);

    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                             .map((this::toDto));
    }

    @Override
    @Transactional
    public UserDTO createUser(UserCreateRequest request) {
        ensureEmailAvailable(request.getEmail(), null);

        User user = new User();
        modelMapper.map(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new UserNotFoundException(id));

        ensureEmailAvailable(request.getEmail(), user.getId());

        modelMapper.map(request, user);
        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new UserNotFoundException(id));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            LOGGER.warn("Invalid current password for user id {}", id);
            throw new InvalidCurrentPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        //userRepository.save(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new EmailNotFoundException(email));
        return toDto(user);
    }

    @Override
    public Page<UserDTO> searchUsers(UserCriteriaRequest request, Pageable pageable) {
        Page<User> users = userRepository.searchUsersByCriteria(request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getStatus(),
                pageable);
        return users.map(this::toDto);
    }

    private void ensureEmailAvailable(String email, Long currentUserId) {
        userRepository.findByEmail(email)
                      .filter(existingUser ->
                              !Objects.equals(existingUser.getId(), currentUserId))
                      .ifPresent(existingUser -> {
                          throw new EmailAlreadyExistsException(email);
                      });
    }

    private UserDTO toDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

}
