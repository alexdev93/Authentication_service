package com.app.myapp.user;

import org.springframework.cache.annotation.Cacheable;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.myapp.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CachePut;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        return userRepository.save(user);
    }

    @Cacheable(value = "users:all") // Caches the list of all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "users:id", key = "#id") // Caches the user by their ID
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    @Cacheable(value = "users:username", key = "#username", unless = "#result == null") // Caches user by username
    public User getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    @CachePut(value = "users:id", key = "#id") // Updates the cache with the new user data
    public User updateUser(String id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setPassword(user.getPassword());
                    existingUser.setEmail(user.getEmail());
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    @CacheEvict(value = "users:id", key = "#id") // Evicts only the specific user from the cache
    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
