package com.app.myapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.app.myapp.dto.AccessTokenResponse;
import com.app.myapp.dto.ForgotPasswordDTO;
import com.app.myapp.dto.LoginRequestDTO;
import com.app.myapp.dto.RefreshTokenRequest;
import com.app.myapp.dto.ResetPasswordDTO;
import com.app.myapp.dto.UserRequestDTO;
import com.app.myapp.model.User;
import com.app.myapp.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRequestDTO userDTO) {
        User registeredUser = authService.register(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        AccessTokenResponse response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AccessTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordDTO entity) {
        if (entity.getEmail() == null || entity.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        return authService.forgotPassword(entity.getEmail());
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<User> resetPassword(@RequestBody ResetPasswordDTO dto) {
        return ResponseEntity.ok(authService.resetPassword(dto));
    }
}
