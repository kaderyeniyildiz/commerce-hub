package com.commercehub.user.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends AbstractException {
    public EmailAlreadyExistsException(String email) {
        super(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXIST", "User with email " + email + " already exists");
    }
}
