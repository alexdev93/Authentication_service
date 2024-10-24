package com.app.myapp.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import com.app.myapp.role.Role;

import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Set;

@Document(collection = "users")
@Data // Generates getters, setters, equals, hashCode, toString
@NoArgsConstructor // Generates a no-args constructor
@AllArgsConstructor
public class User {

    @Id
    private String id; // Using String for MongoDB ID

    @Indexed(unique = true) // Unique constraint on username
    private String username;

    @Indexed(unique = true) // Unique constraint on email
    private String email;

    private String password;

    @DBRef // Reference to Role documents
    private Set<Role> roles; // Set of roles associated with the user
}
