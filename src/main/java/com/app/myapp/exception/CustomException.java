package com.app.myapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus status;

    // Default Constructor
    public CustomException() {
        super();
        this.status = HttpStatus.INTERNAL_SERVER_ERROR; // Default to 500 Internal Server Error
    }

    // Constructor with message
    public CustomException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR; // Default to 500
    }

    // Constructor with message and cause
    public CustomException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    // Constructor with cause
    public CustomException(Throwable cause) {
        super(cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    // Constructor with custom status and message
    public CustomException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    // Constructor with custom status, message, and cause
    public CustomException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
