package com.app.myapp.service;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Simplified and aligned key handling
@Component
public class JwtService {

    private final String SECRET_KEY = "your-secret-key";
    private final String RF_SECRET_KEY = "my-refresh-token";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 365 * 24 * 60 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 10 * 60 * 1000;

    public String generateToken(String id) {
        return createToken(new HashMap<>(), id, SECRET_KEY, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String generateRefreshToken(String id) {
        return createToken(new HashMap<>(), id, RF_SECRET_KEY, REFRESH_TOKEN_EXPIRATION_TIME);
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

    public Boolean validateToken(String token, String id, Boolean isRFToken) {
        String extractedId = extractId(token, isRFToken);
        return (extractedId.equals(id) && !isTokenExpired(token, isRFToken));
    }

    public String extractId(String token, Boolean isRFToken) {
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
