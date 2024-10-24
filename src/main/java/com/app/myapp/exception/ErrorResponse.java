package com.app.myapp.exception;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String details;
    private String path;
    private int status;
}
