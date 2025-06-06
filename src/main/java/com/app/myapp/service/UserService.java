package com.app.myapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.myapp.dto.UserRequestDTO;
import com.app.myapp.dto.UserRequestParams;
import com.app.myapp.enums.RoleName;
import com.app.myapp.exception.NotFoundException;
import com.app.myapp.model.Role;
import com.app.myapp.model.User;
import com.app.myapp.repository.UserRepository;
import com.app.myapp.util.AgregationPipeline;
import com.app.myapp.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final MongoTemplate mongoTemplate;

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

    @Cacheable(value = "users", key = "#userRequestParams.page + '-' + #userRequestParams.size + '-' + #userRequestParams.sortField + '-' + #userRequestParams.sortDirection + '-' + #userRequestParams.searchTerm", unless = "#result.isEmpty()")
    public List<User> getUsersList(UserRequestParams userRequestParams) {
        Criteria criteria = AgregationPipeline.buildCriteria(userRequestParams.getSearchType(),
                userRequestParams.getSearchTerm());

        Aggregation aggregation = AgregationPipeline.buildAggregationPipeline(
                criteria,
                userRequestParams.getSortOrder(),
                userRequestParams.getPage(),
                userRequestParams.getSize(),
                "username", "email", "roles");

        return mongoTemplate.aggregate(aggregation, "users", User.class).getMappedResults();
    }

    @Cacheable(value = "userCount", key = "#userRequestParams.page + '-' + #userRequestParams.size + '-' + #userRequestParams.searchTerm + '-' + #userRequestParams.searchType", unless = "#result == 0")
    public long getUserCount(UserRequestParams userRequestParams) {
        Criteria criteria = AgregationPipeline.buildCriteria("username", userRequestParams.getSearchTerm());
        return mongoTemplate.count(Query.query(criteria), User.class);
    }

    public Page<User> getUsers(UserRequestParams userRequestParams) {
        List<User> users = getUsersList(userRequestParams);
        long total = getUserCount(userRequestParams);
        Pageable pageable = PageRequest.of(userRequestParams.getPage(), userRequestParams.getSize());

        return new PageImpl<>(users, pageable, total);
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    public Optional<User> getMe(String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        JwtUtil jwtUtil = new JwtUtil();
        String username = jwtUtil.extractUsername(jwt, false);
        return getUserByUserName(username);
    }

    public Optional<User> getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @CacheEvict(value = "users", allEntries = true)
    public User updateUser(String id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setPassword(user.getPassword());
                    existingUser.setEmail(user.getEmail());
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    @CacheEvict(value = "users", allEntries = true)
    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Role> getUserRoles(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));

        return user.getRoles();
    }

    @CacheEvict(value = "users", allEntries = true)
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
