package com.commercehub.user.exception;

import org.springframework.http.HttpStatus;

public class EmailNotExist extends AbstractException {
    public EmailNotExist(String email) {
        super(HttpStatus.NOT_FOUND, "EMAIL_NOT_FOUND", "User with email " + email + " was not found");
    }
}
