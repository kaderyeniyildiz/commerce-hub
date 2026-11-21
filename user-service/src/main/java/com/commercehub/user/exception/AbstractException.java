package com.commercehub.user.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    protected AbstractException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public final HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public final String getErrorCode() {
        return errorCode;
    }

}
