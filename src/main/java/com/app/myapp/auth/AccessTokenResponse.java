package com.app.myapp.auth;

import java.util.List;

import com.app.myapp.role.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {

    private String access_token; // JWT token
    private String refresh_token; // JWT token
    private String username; // Optionally include the username or any other information you want to return
    private List<Role> roles; 
}
