package com.commercehub.user.service;

import com.commercehub.user.dto.UserDTO;
import com.commercehub.user.request.UserRequest;

import java.util.List;

public interface UserServiceImpl {

    UserDTO getUser(Long id);

    List<UserDTO> getAllUsers();

    UserDTO save(UserRequest request);

    UserDTO update(UserRequest request, Long id);

    boolean delete(Long id);
}
