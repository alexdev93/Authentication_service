package com.app.myapp.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// import io.jsonwebtoken.ExpiredJwtException;
// import io.jsonwebtoken.JwtException;
// import io.jsonwebtoken.SignatureException;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = createErrorResponse(ex.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Custom Exception Handler
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, WebRequest request) {
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    // // Handle expired JWT token
    // @ExceptionHandler(ExpiredJwtException.class)
    // public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException
    // ex) {
    // return new ResponseEntity<>("Token has expired", HttpStatus.UNAUTHORIZED);
    // }

    // // Handle invalid JWT signature
    // @ExceptionHandler(SignatureException.class)
    // public ResponseEntity<String> handleSignatureException(SignatureException ex)
    // {
    // return new ResponseEntity<>("Invalid token signature",
    // HttpStatus.UNAUTHORIZED);
    // }

    // // Handle generic JWT exceptions
    // @ExceptionHandler(JwtException.class)
    // public ResponseEntity<String> handleJwtException(JwtException ex) {
    // return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
    // }

    // Handle UsernameAlreadyExistsException
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex,
            WebRequest request) {
        ErrorResponse errorResponse = createErrorResponse(
                "Username already exists",
                request,
                HttpStatus.CONFLICT // 409 Conflict
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle Invalid Credntial
    @ExceptionHandler(InvalidCredntial.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(InvalidCredntial ex,
            WebRequest request) {
        ErrorResponse errorResponse = createErrorResponse(
                "Invalid credintial",
                request,
                HttpStatus.FORBIDDEN // 403 forbidden
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
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
