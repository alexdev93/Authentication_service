package com.app.myapp.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.myapp.user.User;
import com.app.myapp.user.UserRequestDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth") // Base route for authentication
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRequestDTO userDTO) {
        User registeredUser = authService.register(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response); // Return JWT token
    }
}
