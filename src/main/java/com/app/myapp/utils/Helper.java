package com.app.myapp.utils;

import java.io.IOException;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Helper {

    private final ResponseHandler responseHandler;
    private final JavaMailSender mailSender;

    public void handleServletResponse(HttpServletRequest request, HttpServletResponse response,
            int statusCode, String error, String message) throws IOException {
        responseHandler.writeErrorResponse(request, response, statusCode, error, message);
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
