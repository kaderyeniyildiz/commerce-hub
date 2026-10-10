package com.commercehub.user.service;

import com.commercehub.user.dto.UserDTO;
import com.commercehub.user.entity.User;
import com.commercehub.user.exception.UserNotFoundException;
import com.commercehub.user.exception.InvalidCurrentPasswordException;
import com.commercehub.user.repository.UserRepository;
import com.commercehub.user.request.ChangePasswordRequest;
import com.commercehub.user.request.UserCreateRequest;
import com.commercehub.user.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return modelMapper.map(user, UserDTO.class);

    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map((user -> modelMapper.map(user, UserDTO.class)));
    }

    @Override
    public UserDTO save(UserCreateRequest request) {
        User user = new User();
        modelMapper.map(request, user);
        String encode = passwordEncoder.encode(request.getPassword());
        user.setPassword(encode);
        User save = userRepository.save(user);
        return modelMapper.map(save, UserDTO.class);
    }

    @Override
    public UserDTO update(UserUpdateRequest request, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        modelMapper.map(request, user);
        User save = userRepository.save(user);
        return modelMapper.map(save, UserDTO.class);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
