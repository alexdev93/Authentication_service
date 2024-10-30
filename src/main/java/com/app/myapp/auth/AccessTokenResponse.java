package com.app.myapp.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {

    private String id;
    private String access_token; // JWT token
    private String refresh_token; // JWT token
}