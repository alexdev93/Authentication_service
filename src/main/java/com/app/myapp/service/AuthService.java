package com.app.myapp.service;

import com.app.myapp.dto.AccessTokenResponse;
import com.app.myapp.dto.LoginRequestDTO;
import com.app.myapp.dto.ResetPasswordDTO;
import com.app.myapp.dto.UserRequestDTO;
import com.app.myapp.exception.CustomException;
import com.app.myapp.exception.InvalidCredential;
import com.app.myapp.model.User;
import com.app.myapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtUtil;

    public User register(UserRequestDTO userDTO) {
        // Check if the user already exists by username
        Optional<User> existingUserByUsername = userService.getUserByUserName(userDTO.getUsername());
        if (existingUserByUsername.isPresent()) {
            throw new CustomException("Username '" + userDTO.getUsername() + "' is already taken.");
        }

        // Check if the user already exists by email
        Optional<User> existingUserByEmail = userService.getUserByEmail(userDTO.getEmail());
        if (existingUserByEmail.isPresent()) {
            throw new CustomException("Email '" + userDTO.getEmail() + "' is already taken.");
        }

        // If neither username nor email exists, proceed to create the new user
        return userService.createUser(userDTO);
    }

    public AccessTokenResponse login(LoginRequestDTO loginRequestDTO) {
        User user = userService.getUserByEmail(loginRequestDTO.getEmail()).get();

        if (user != null
                && passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            return generateTokenResponse(user);
        } else {
            throw new InvalidCredential();
        }
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        String id = jwtUtil.extractId(refreshToken, true);
        boolean isValid = jwtUtil.validateToken(refreshToken, id, true);
        User user = userService.getUserById(id);

        return isValid ? generateTokenResponse(user) : null;
    }

    public AccessTokenResponse generateTokenResponse(User user) {
        String id = user.getId();
        String token = jwtUtil.generateToken(id);
        String refreshToken = jwtUtil.generateRefreshToken(id);
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setAccess_token(token);
        accessTokenResponse.setRefresh_token(refreshToken);
        accessTokenResponse.setId(user.getId());
        // accessTokenResponse.setRoles(user.getRoles());
        return accessTokenResponse;
    }

    public String forgotPassword(String email) {
        User user = userService.getUserByEmail(email).get();

        String resetToken = java.util.UUID.randomUUID().toString();
        long expiry = System.currentTimeMillis() + 15 * 60 * 1000; // 15 minutes

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(expiry);
        userRepository.save(user);

        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;

        return "Password reset link: " + resetLink;
    }

    public User resetPassword(ResetPasswordDTO resetPasswordDTO) {
        Optional<User> userOpt = userService.getUserByResetToken(resetPasswordDTO.getResetToken());
        if (userOpt.isEmpty()) {
            throw new CustomException("Invalid or expired reset token.");
        }
        User user = userOpt.get();

        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userService.clearPasswordResetToken(user.getId());

        return userRepository.save(user);
    }
}
