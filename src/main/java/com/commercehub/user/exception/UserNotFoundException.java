package com.commercehub.user.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AbstractException{
    //private String[] messageArgs;

//    public UserNotFoundException(String...args) {
//        this.messageArgs = args;
//    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

//    @Override
//    public String getExceptionCode() {
//        return "USER001";
//    }

    @Override
    public String getMessage() {
        return "User Not Found exception UserId: {}";
    }
//
//    @Override
//    public String[] getMessageArgs() {
//        return this.messageArgs;
//    }
}
