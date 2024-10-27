package com.app.myapp.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.myapp.enums.RoleName;
import com.app.myapp.exception.NotFoundException;
import com.app.myapp.role.Role;
import com.app.myapp.role.RoleService;

import lombok.RequiredArgsConstructor;

import static com.app.myapp.utils.AgregationPipeline.*;

@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final MongoTemplate mongoTemplate;

    @PreAuthorize("hasAuthrority('ADMIN')")
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

    public Page<User> getUsers(UserRequestParams userRequestParams) {

        Criteria criteria = new Criteria();
        String searchTerm = userRequestParams.getSearchTerm();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            criteria.orOperator(
                    Criteria.where("username").regex(searchTerm, "i"),
                    Criteria.where("email").regex(searchTerm, "i"));
        }

        Aggregation aggregation = buildAggregationPipeline(
                criteria,
                userRequestParams.getSortOrder(),
                userRequestParams.getPage(),
                userRequestParams.getSize(),
                "username", "email", "roles");

        List<User> users = mongoTemplate.aggregate(aggregation, "users", User.class).getMappedResults();

        long total = mongoTemplate.count(Query.query(criteria), User.class);
        Pageable pageable = PageRequest.of(userRequestParams.getPage(), userRequestParams.getSize());

        return new PageImpl<>(users, pageable, total);
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    public User getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateUser(String id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setPassword(user.getPassword());
                    existingUser.setEmail(user.getEmail());
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

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
