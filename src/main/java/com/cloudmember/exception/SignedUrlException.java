package com.cloudmember.exception;

import org.springframework.http.HttpStatus;

public class SignedUrlException extends ServiceException {
    public SignedUrlException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public SignedUrlException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
        initCause(cause);
    }
}
