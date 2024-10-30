package com.app.myapp.auth;

import com.app.myapp.exception.InvalidCredential;
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
        User user = userService.getUserByEmail(loginRequestDTO.getEmail());

        if (user != null
                && passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            return generateTokenResponse(user);
        } else {
            throw new InvalidCredential();
        }
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken, true);
        boolean isValid = jwtUtil.validateToken(refreshToken, username, true);
        User user = userService.getUserByUserName(username);

        return isValid ? generateTokenResponse(user) : null;
    }

    public AccessTokenResponse generateTokenResponse(User user) {
        String username = user.getUsername();
        String token = jwtUtil.generateToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setAccess_token(token);
        accessTokenResponse.setRefresh_token(refreshToken);
        accessTokenResponse.setId(user.getId());
        // accessTokenResponse.setRoles(user.getRoles());
        return accessTokenResponse;
    }
}
