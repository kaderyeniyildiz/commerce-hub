package com.commercehub.user.service;

import com.commercehub.user.dto.UserDTO;
import com.commercehub.user.entity.User;
import com.commercehub.user.exception.UserNotFoundException;
import com.commercehub.user.repository.UserRepository;
import com.commercehub.user.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserDTO.class);

    }

    @Override
    public List<UserDTO> getAllUsers() {
        return modelMapper.map(userRepository.findAll(),
                new TypeToken<List<User>>() {
                }.getType());
    }

    @Override
    public UserDTO save(UserRequest request) {
        User user = new User();
        modelMapper.map(request, user);
        String encode = passwordEncoder.encode(request.getPassword());
        user.setPassword(encode);
        User save = userRepository.save(user);
        return modelMapper.map(save, UserDTO.class);
    }

    @Override
    public UserDTO update(UserRequest request, Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        modelMapper.map(request, user);
        User save = userRepository.save(user);
        return modelMapper.map(save, UserDTO.class);
    }

    @Override
    public boolean delete(Long id) {
        userRepository.deleteById(id);
        return true;
    }
}
