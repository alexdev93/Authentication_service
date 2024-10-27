package com.app.myapp.utils;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Helper {

    private final ResponseHandler responseHandler;

    public void handleServletResponse(HttpServletRequest request, HttpServletResponse response,
            int statusCode, String error, String message) throws IOException {
        responseHandler.writeErrorResponse(request, response, statusCode, error, message);
    }
}
