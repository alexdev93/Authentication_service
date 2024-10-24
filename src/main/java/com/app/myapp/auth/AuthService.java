package com.app.myapp.auth;

import com.app.myapp.user.*;
import com.app.myapp.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService; // Assume UserService handles user-related DB operations
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User register(UserRequestDTO userDTO) {
        // Check if user already exists
        if (userService.getUserByUserName(userDTO.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        return userService.createUser(userDTO); // Save user to DB
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User userOptional = userService.getUserByUserName(loginRequestDTO.getUsername());

        if (userOptional != null
                && passwordEncoder.matches(loginRequestDTO.getPassword(), userOptional.getPassword())) {
            String username = loginRequestDTO.getUsername();
            String token = jwtUtil.generateToken(username);
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
            loginResponseDTO.setToken(token);
            loginResponseDTO.setUsername(username);
            return loginResponseDTO;
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
