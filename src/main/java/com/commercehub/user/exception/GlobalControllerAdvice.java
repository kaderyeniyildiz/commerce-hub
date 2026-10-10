package com.commercehub.user.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<String> handleBusinessException(AbstractException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ex.getMessage());
    }
}
