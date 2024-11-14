package com.app.myapp;

import com.app.myapp.auth.AccessTokenResponse;
import com.app.myapp.auth.AuthService;
import com.app.myapp.auth.LoginRequestDTO;
import com.app.myapp.exception.CustomException;
import com.app.myapp.exception.InvalidCredential;
import com.app.myapp.user.User;
import com.app.myapp.user.UserRequestDTO;
import com.app.myapp.user.UserService;
import com.app.myapp.utils.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_whenUsernameIsTaken_shouldThrowCustomException() {
        UserRequestDTO userDTO = new UserRequestDTO("takenUsername", "user@example.com", "password123");

        when(userService.getUserByUserName(userDTO.getUsername())).thenReturn(Optional.of(new User()));

        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.register(userDTO);
        });

        assertEquals("Username '" + userDTO.getUsername() + "' is already taken.", exception.getMessage());
    }

    @Test
    void register_whenEmailIsTaken_shouldThrowCustomException() {
        UserRequestDTO userDTO = new UserRequestDTO("uniqueUsername", "taken@example.com", "password123");

        when(userService.getUserByEmail(userDTO.getEmail())).thenReturn(Optional.of(new User()));

        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.register(userDTO);
        });

        assertEquals("Email '" + userDTO.getEmail() + "' is already taken.", exception.getMessage());
    }

    @Test
    void register_whenUserIsValid_shouldCreateUser() {
        UserRequestDTO userDTO = new UserRequestDTO("uniqueUsername", "unique@example.com", "password123");
        User createdUser = new User("uniqueUsername", "unique@example.com", "hashedPassword");

        when(userService.getUserByUserName(userDTO.getUsername())).thenReturn(Optional.empty());
        when(userService.getUserByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userService.createUser(userDTO)).thenReturn(createdUser);

        User result = authService.register(userDTO);

        assertEquals(createdUser, result);
    }

    @Test
    void login_whenCredentialsAreValid_shouldReturnAccessTokenResponse() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user@example.com", "password123");
        User user = new User("username", "user@example.com", "hashedPassword");

        when(userService.getUserByEmail(loginRequestDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(user.getUsername())).thenReturn("refreshToken");

        AccessTokenResponse result = authService.login(loginRequestDTO);

        assertNotNull(result);
        assertEquals("accessToken", result.getAccess_token());
        assertEquals("refreshToken", result.getRefresh_token());
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void login_whenCredentialsAreInvalid_shouldThrowInvalidCredentialException() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user@example.com", "wrongPassword");
        User user = new User("username", "user@example.com", "hashedPassword");

        when(userService.getUserByEmail(loginRequestDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredential.class, () -> {
            authService.login(loginRequestDTO);
        });
    }

    @Test
    void refreshToken_whenTokenIsValid_shouldReturnNewAccessTokenResponse() {
        String refreshToken = "validRefreshToken";
        String username = "username";
        User user = new User(username, "user@example.com", "hashedPassword");

        when(jwtUtil.extractUsername(refreshToken, true)).thenReturn(username);
        when(jwtUtil.validateToken(refreshToken, username, true)).thenReturn(true);
        when(userService.getUserByUserName(username)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(username)).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken(username)).thenReturn("newRefreshToken");

        AccessTokenResponse result = authService.refreshToken(refreshToken);

        assertNotNull(result);
        assertEquals("newAccessToken", result.getAccess_token());
        assertEquals("newRefreshToken", result.getRefresh_token());
    }

    @Test
    void refreshToken_whenTokenIsInvalid_shouldReturnNull() {
        String refreshToken = "invalidRefreshToken";
        String username = "username";

        when(jwtUtil.extractUsername(refreshToken, true)).thenReturn(username);
        when(jwtUtil.validateToken(refreshToken, username, true)).thenReturn(false);

        AccessTokenResponse result = authService.refreshToken(refreshToken);

        assertNull(result);
    }
}
