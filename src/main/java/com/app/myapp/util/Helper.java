package com.app.myapp.util;

import java.io.IOException;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.app.myapp.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Helper {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void writeErrorResponse(HttpServletRequest request, HttpServletResponse response,
            int statusCode, String error, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(statusCode)
                .message(message)
                .details(error)
                .path(request.getRequestURI())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    public Object convertValue(Class<?> targetType, Object value) {
        if (value == null)
            return null;

        if (targetType.equals(String.class))
            return value.toString();
        if (targetType.equals(Long.class))
            return Long.parseLong(value.toString());
        if (targetType.equals(Integer.class))
            return Integer.parseInt(value.toString());
        if (targetType.equals(Boolean.class))
            return Boolean.parseBoolean(value.toString());

        throw new IllegalArgumentException("Unsupported type: " + targetType.getName());
    }

    @Async
    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Email Verification");
        message.setText("Click the following link to verify your email: " + verificationUrl);

        // Send email asynchronously
        mailSender.send(message);
    }
}
