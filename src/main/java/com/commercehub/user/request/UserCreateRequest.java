package com.commercehub.user.request;

import com.commercehub.user.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    @NotBlank
    @Size(max = 200)
    private String firstName;

    @NotBlank
    @Size(max = 200)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 200)
    private String email;

    @NotBlank
    @Size(max = 200)
    private String password;

    @Size(max = 20)
    private String phoneNumber;

    @NotNull
    @Size(max = 20)
    private UserStatus status;
}
