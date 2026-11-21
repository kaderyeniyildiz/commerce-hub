package com.commercehub.user.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AbstractException{
    public UserNotFoundException(Long userId) {
        super(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User with id " + userId + " was not found");
    }
}
