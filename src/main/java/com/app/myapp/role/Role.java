package com.app.myapp.role;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.app.myapp.enums.RoleName;

import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    private String id;

    @Indexed(unique = true)
    private RoleName name;

}
