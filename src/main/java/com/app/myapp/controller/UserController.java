package com.app.myapp.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.myapp.dto.UserRequestParams;
import com.app.myapp.model.Role;
import com.app.myapp.model.User;
import com.app.myapp.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Page<User> getUsers(@Valid @ModelAttribute UserRequestParams userRequestParams) {
        return userService.getUsers(userRequestParams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<Role>> getUserRoles(@PathVariable String userId) {
        List<Role> roles = userService.getUserRoles(userId);
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{userId}/assign-roles")
    public ResponseEntity<User> assignRolesToUser(
            @PathVariable String userId,
            @RequestParam List<String> roleIds) {

        User updatedUser = userService.assignRoles(userId, roleIds);
        return ResponseEntity.ok(updatedUser);
    }

}
