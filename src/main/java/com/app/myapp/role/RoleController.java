package com.app.myapp.role;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @CacheEvict(value = "roles", allEntries = true) // Evict all roles when a new role is created
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role savedRole = roleService.createRole(role);
        return ResponseEntity.status(201).body(savedRole);
    }

    @GetMapping
    @Cacheable(value = "roles") // Caches the result of this method
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @Cacheable(value = "roles", key = "#id") // Caches the role by ID
    public ResponseEntity<Role> getRoleById(@PathVariable String id) {
        Role role = roleService.getRoleById(id);
        return role != null ? ResponseEntity.ok(role) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @CachePut(value = "roles", key = "#id") // Updates the cache with the modified role
    public ResponseEntity<Role> updateRole(@PathVariable String id, @RequestBody Role role) {
        Role updatedRole = roleService.updateRole(id, role);
        return updatedRole != null ? ResponseEntity.ok(updatedRole) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "roles", key = "#id") // Removes the role from cache
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        boolean deleted = roleService.deleteRole(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
