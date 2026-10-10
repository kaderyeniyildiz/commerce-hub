package com.commercehub.user.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractException extends RuntimeException {

    public abstract HttpStatus getHttpStatus();

    //public abstract String getExceptionCode();

    public abstract String getMessage();

   // public abstract String[] getMessageArgs();

}
