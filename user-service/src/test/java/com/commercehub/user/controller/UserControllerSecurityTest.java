package com.commercehub.user.controller;

import com.commercehub.user.config.SecurityConfig;
import com.commercehub.user.dto.UserDTO;
import com.commercehub.user.request.ChangePasswordRequest;
import com.commercehub.user.request.UserCreateRequest;
import com.commercehub.user.request.UserCriteriaRequest;
import com.commercehub.user.request.UserUpdateRequest;
import com.commercehub.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, UserControllerSecurityTest.TestUserServiceConfiguration.class})
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestUserServiceConfiguration {

        @Bean
        UserService userService() {
            return new UserService() {
                @Override
                public UserDTO getUserById(Long id) {
                    return new UserDTO();
                }

                @Override
                public Page<UserDTO> getAllUsers(Pageable pageable) {
                    return Page.empty(pageable);
                }

                @Override
                public UserDTO createUser(UserCreateRequest request) {
                    return new UserDTO();
                }

                @Override
                public UserDTO updateUser(Long id, UserUpdateRequest request) {
                    return new UserDTO();
                }

                @Override
                public void deleteUser(Long id) {
                }

                @Override
                public void changePassword(Long id, ChangePasswordRequest request) {
                }

                @Override
                public UserDTO getUserByEmail(String email) {
                    return new UserDTO();
                }

                @Override
                public Page<UserDTO> searchUsers(UserCriteriaRequest request, Pageable pageable) {
                    return Page.empty(pageable);
                }
            };
        }
    }

    @Test
    void getUserIsAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createUserIsAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content("""
                                {
                                  "firstName": "Ada",
                                  "lastName": "Lovelace",
                                  "email": "ada@example.com",
                                  "password": "valid-password",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void updateUserIsAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(put("/api/v1/users/1")
                        .contentType("application/json")
                        .content("""
                                {
                                  "firstName": "Ada",
                                  "lastName": "Lovelace",
                                  "email": "ada@example.com",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserIsAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }
}
