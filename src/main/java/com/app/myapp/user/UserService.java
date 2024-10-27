package com.app.myapp.user;

// import org.springframework.cache.annotation.Cacheable;
// import org.springframework.cache.annotation.Caching;
// import org.springframework.cache.annotation.CacheEvict;
// import org.springframework.cache.annotation.CachePut;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.myapp.enums.RoleName;
import com.app.myapp.exception.NotFoundException;
import com.app.myapp.role.Role;
import com.app.myapp.role.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    // @CacheEvict(value = "users:all", allEntries = true)
    public User createUser(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        Role role = roleService.getRoleByName(RoleName.USER.name());
        user.setRoles(new ArrayList<Role>());
        user.getRoles().add(role);

        return userRepository.save(user);
    }

    // @Cacheable(value = "users:all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // @Cacheable(value = "users:id", key = "#id")
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    // @Cacheable(value = "users:username", key = "#username", unless = "#result ==
    // null")
    public User getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    // @CachePut(value = "users:id", key = "#id")
    public User updateUser(String id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setPassword(user.getPassword());
                    existingUser.setEmail(user.getEmail());
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    // @Caching(evict = {
    // @CacheEvict(value = "users:all", allEntries = true),
    // @CacheEvict(value = "users:roles", allEntries = true),
    // @CacheEvict(value = "users:id", key = "#id"),
    // })
    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // @Cacheable(value = "users:roles", key = "#id")
    public List<Role> getUserRoles(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));

        return user.getRoles();
    }

    // @Caching(evict = {
    // @CacheEvict(value = "users:id", key = "#id"),
    // @CacheEvict(value = "users:roles", key = "#id")
    // })
    public User assignRoles(String id, List<String> roleIds) {
        User user = this.getUserById(id);

        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }

        List<Role> roles = roleIds.stream()
                .map(roleService::getRoleById)
                .collect(Collectors.toList());

        roles.forEach(role -> {
            if (!user.getRoles().contains(role)) {
                user.getRoles().add(role);
            }
        });

        return userRepository.save(user);
    }

}
