package com.commercehub.user.service;

import com.commercehub.user.dto.UserDTO;
import com.commercehub.user.request.UserCreateRequest;
import com.commercehub.user.request.UserUpdateRequest;
import com.commercehub.user.request.ChangePasswordRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {

    UserDTO getUser(Long id);

    Page<UserDTO> getAllUsers(Pageable pageable);

    UserDTO save(UserCreateRequest request);

    UserDTO update(UserUpdateRequest request, Long id);

    void delete(Long id);

    void changePassword(Long id, ChangePasswordRequest request);
}
