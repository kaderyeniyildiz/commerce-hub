package com.commercehub.user.request;

import com.commercehub.user.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^\\+?[0-9 ()-]{7,20}$",
            message = "Phone number must contain only digits, spaces, +, parentheses, or hyphens"
    )
    private String phoneNumber;

    @NotNull
    private UserStatus status;
}
