package com.app.myapp.utils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "your-secret-key";
    private final String REFRESH_SECRET_KEY = "your-refresh-secret-key";

    // Define expiration time constants
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1 * 60 * 1000; // 1 minute
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutes

    // Generate a token
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    // Create a token
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Validate the token
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public boolean validateToken(String token, boolean isRefreshToken) {
        try {
            Jwts.parser().setSigningKey(isRefreshToken ? REFRESH_SECRET_KEY : SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException(e.getMessage());
        }
    }

    // Extract username from the token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
