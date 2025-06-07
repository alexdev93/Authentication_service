package com.app.myapp.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.app.myapp.dto.UserRequestParams;
import com.app.myapp.model.Role;
import com.app.myapp.model.User;
import com.app.myapp.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;

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

    @GetMapping("/me")
    public ResponseEntity<User> thisUser(@NonNull HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        User user = userService.getMe(authorizationHeader);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        User updatedUser = userService.updateUser(id, updates);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/me/update")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<User> updateMe(@NonNull HttpServletRequest request,
            @RequestBody Map<String, Object> updates) {
        final String authorizationHeader = request.getHeader("Authorization");
        User thisUser = userService.getMe(authorizationHeader);
        User updatedUser = userService.updateUser(thisUser.getId(), updates);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<Role>> getUserRoles(@PathVariable String userId) {
        List<Role> roles = userService.getUserRoles(userId);
        return ResponseEntity.ok(roles);
    }

    @PatchMapping("/{userId}/assign-roles")
    public ResponseEntity<User> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {

        User updatedUser = userService.assignRoles(userId, roleIds);
        return ResponseEntity.ok(updatedUser);
    }

}
