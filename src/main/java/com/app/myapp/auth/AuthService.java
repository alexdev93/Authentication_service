package com.app.myapp.auth;

import com.app.myapp.exception.CustomException;
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
        User userOptional = userService.getUserByUserName(loginRequestDTO.getUsername());

        if (userOptional != null
                && passwordEncoder.matches(loginRequestDTO.getPassword(), userOptional.getPassword())) {
            return generateTokenResponse(userOptional.getUsername());
        } else {
            throw new InvalidCredntial();
        }
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        boolean isValid = jwtUtil.validateToken(refreshToken, true);
        String username = jwtUtil.extractUsername(refreshToken);

        User user = userService.getUserByUserName(username);
        if (!isValid && user == null) {
            throw new CustomException("INVALID_TOKEN || USER_NOT_FIUND");
        }
        return generateTokenResponse(username);
    }

    public AccessTokenResponse generateTokenResponse(String username) {
        String token = jwtUtil.generateToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setAccess_token(token);
        accessTokenResponse.setRefresh_token(refreshToken);
        accessTokenResponse.setUsername(username);
        return accessTokenResponse;
    }
}
