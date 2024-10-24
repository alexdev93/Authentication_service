package com.app.myapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Generic Exception occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = createErrorResponse(ex.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Custom Exception Handler
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, WebRequest request) {
        logger.error("Custom Exception occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = createErrorResponse(ex.getMessage(), request, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Resource not found",
                ex.getMessage(),
                null, // Optional: you can set the request path here
                HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Method to create a standardized error response
    private ErrorResponse createErrorResponse(String message, WebRequest request, HttpStatus status) {
        return new ErrorResponse(
                message,
                request.getDescription(false),
                request.getContextPath(),
                status.value());
    }
}
