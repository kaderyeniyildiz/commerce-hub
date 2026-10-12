package com.commercehub.user.service;

import com.commercehub.user.dto.UserDTO;
import com.commercehub.user.request.UserCreateRequest;
import com.commercehub.user.request.UserCriteriaRequest;
import com.commercehub.user.request.UserUpdateRequest;
import com.commercehub.user.request.ChangePasswordRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {

    UserDTO getUserById(Long id);

    Page<UserDTO> getAllUsers(Pageable pageable);

    UserDTO createUser(UserCreateRequest request);

    UserDTO updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    void changePassword(Long id, ChangePasswordRequest request);

    UserDTO getUserByEmail(String email);

    Page<UserDTO> searchUsers(UserCriteriaRequest request, Pageable pageable);

}
