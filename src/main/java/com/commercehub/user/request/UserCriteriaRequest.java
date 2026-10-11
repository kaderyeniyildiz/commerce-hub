package com.commercehub.user.request;

import com.commercehub.user.enums.UserStatus;
import lombok.Getter;

@Getter
public class UserCriteriaRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserStatus status;
}
