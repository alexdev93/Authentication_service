package com.app.myapp.role;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;



@Document(collection = "roles")
@Data // Generates getters, setters, equals, hashCode, toString
@NoArgsConstructor // Generates a no-args constructor
@AllArgsConstructor // Generates a constructor with all fields
public class Role {

    @Id
    private String id; // Using String for MongoDB ID

    @Indexed(unique = true) // Make name unique
    private String name; // The name of the role (e.g., "USER", "ADMIN")

}
