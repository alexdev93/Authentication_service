package com.app.myapp.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import com.app.myapp.role.Role;

@Data // Generates getters, setters, equals, hashCode, toString
@NoArgsConstructor // Generates a no-args constructor
public class UserRequestDTO {
    private String username; // User's username
    private String email; // User's email
    private String password; // User's password
    private Set<Role> roles; // Set of role IDs
}
