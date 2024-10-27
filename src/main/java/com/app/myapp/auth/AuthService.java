package com.app.myapp.auth;

import com.app.myapp.exception.InvalidCredntial;
import com.app.myapp.exception.UsernameAlreadyExistsException;
import com.app.myapp.user.*;
import com.app.myapp.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User register(UserRequestDTO userDTO) {
        if (userService.getUserByUserName(userDTO.getUsername()) != null) {
            throw new UsernameAlreadyExistsException();
        }

        return userService.createUser(userDTO);
    }

    public AccessTokenResponse login(LoginRequestDTO loginRequestDTO) {
        User user = userService.getUserByUserName(loginRequestDTO.getUsername());

        if (user != null
                && passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            return generateTokenResponse(user.getUsername());
        } else {
            throw new InvalidCredntial();
        }
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        boolean isValid = jwtUtil.validateToken(refreshToken, username);

        return isValid ? generateTokenResponse(username) : null;
    }

    public AccessTokenResponse generateTokenResponse(String username) {
        String token = jwtUtil.generateToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setAccess_token(token);
        accessTokenResponse.setRefresh_token(refreshToken);
        accessTokenResponse.setUsername(username);
        accessTokenResponse.setRoles(userService.getUserByUserName(username).getRoles());
        return accessTokenResponse;
    }
}
