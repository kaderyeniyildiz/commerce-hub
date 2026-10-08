package com.commercehub.user.request;

import com.commercehub.user.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotBlank
    @Email
    @Pattern(regexp = ".*@.*\\..*")
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private String phoneNumber;

    @NotNull
    private UserStatus status;
}
