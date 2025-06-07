// package com.app.myapp;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import com.app.myapp.service.JwtService;

// import static org.junit.jupiter.api.Assertions.*;

// class JwtUtilTest {

//     private JwtService jwtUtil;
//     private String username = "testUser";

//     @BeforeEach
//     void setUp() {
//         jwtUtil = new JwtService();
//     }

//     @Test
//     void generateToken_shouldGenerateValidAccessToken() {

//         String token = generateToken();

//         // Check that the token is not null or empty
//         assertNotNull(token);
//         assertFalse(token.isEmpty());

//         // Validate the extracted username matches
//         String extractedUsername = extractUsername(token, false);
//         assertEquals(username, extractedUsername);
//     }

//     @Test
//     void validateToken_shouldReturnTrueForValidToken() {
//         String token = generateToken();

//         // Validate the token
//         Boolean isValid = validateToken(token, username, false);
//         assertTrue(isValid);
//     }

//     private String generateToken() {
//         return jwtUtil.generateToken(username);
//     }

//     private String extractUsername(String token, Boolean isRFToken) {
//         return jwtUtil.extractId(token, isRFToken);
//     }

//     private Boolean validateToken(String token, String username, Boolean isRFToken) {
//         return jwtUtil.validateToken(token, username, false);
//     }

// }
