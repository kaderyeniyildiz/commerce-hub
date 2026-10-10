package com.commercehub.user.exception;

import org.springframework.http.HttpStatus;

public class InvalidCurrentPasswordException extends AbstractException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return "Current password is incorrect";
    }
}
