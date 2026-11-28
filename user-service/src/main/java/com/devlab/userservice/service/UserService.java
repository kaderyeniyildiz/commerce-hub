package com.devlab.userservice.service;

import com.devlab.userservice.dto.UserDTO;
import com.devlab.userservice.request.UserCreateRequest;
import com.devlab.userservice.request.UserCriteriaRequest;
import com.devlab.userservice.request.UserUpdateRequest;
import com.devlab.userservice.request.ChangePasswordRequest;
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
