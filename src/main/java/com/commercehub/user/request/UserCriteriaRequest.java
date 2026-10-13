package com.commercehub.user.request;

import com.commercehub.user.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCriteriaRequest {
    @Size(max = 200)
    private String firstName;

    @Size(max = 200)
    private String lastName;

    @Email
    @Size(max = 200)
    private String email;

    @Size(max = 20)
    @Pattern(
            regexp = "^\\+?[0-9 ()-]{7,20}$",
            message = "Phone number must contain only digits, spaces, +, parentheses, or hyphens"
    )
    private String phoneNumber;
    private UserStatus status;
}
