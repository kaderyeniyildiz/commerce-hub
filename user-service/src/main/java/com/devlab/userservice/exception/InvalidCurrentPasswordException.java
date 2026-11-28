package com.devlab.userservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidCurrentPasswordException extends AbstractException {

    public InvalidCurrentPasswordException() {
        super(HttpStatus.BAD_REQUEST, "INVALID_CURRENT_PASSWORD", "Current password is incorrect");
    }
}
