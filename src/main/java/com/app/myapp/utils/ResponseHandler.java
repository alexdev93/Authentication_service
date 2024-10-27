package com.app.myapp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ResponseHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void writeErrorResponse(HttpServletRequest request, HttpServletResponse response,
            int statusCode, String error, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", statusCode);
        responseBody.put("error", error);
        responseBody.put("message", message);
        responseBody.put("path", request.getRequestURI());

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
