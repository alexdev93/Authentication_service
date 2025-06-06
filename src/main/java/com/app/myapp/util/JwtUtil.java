package com.app.myapp.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Simplified and aligned key handling
@Component
public class JwtUtil {

    private final String SECRET_KEY = "your-secret-key";
    private final String RF_SECRET_KEY = "my-refresh-token";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 60 * 1000; // 1 minute
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 10 * 60 * 1000; // 5 minutes

    public String generateToken(String username) {
        return createToken(new HashMap<>(), username, SECRET_KEY, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String generateRefreshToken(String username) {
        return createToken(new HashMap<>(), username, RF_SECRET_KEY, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private String createToken(Map<String, Object> claims, String subject, String key, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
    }

    public Boolean validateToken(String token, String username, Boolean isRFToken) {
        String extractedUsername = extractUsername(token, isRFToken);
        return (extractedUsername.equals(username) && !isTokenExpired(token, isRFToken));
    }

    public String extractUsername(String token, Boolean isRFToken) {
        return extractAllClaims(token, isRFToken).getSubject();
    }

    private Boolean isTokenExpired(String token, Boolean isRFToken) {
        return extractAllClaims(token, isRFToken).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token, Boolean isRFToken) {
        return Jwts.parser()
                .setSigningKey((isRFToken ? RF_SECRET_KEY : SECRET_KEY).getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}
