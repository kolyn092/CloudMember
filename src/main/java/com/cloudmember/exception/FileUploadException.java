package com.cloudmember.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileUploadException extends ServiceException {

    public FileUploadException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
        initCause(cause);
    }
}
