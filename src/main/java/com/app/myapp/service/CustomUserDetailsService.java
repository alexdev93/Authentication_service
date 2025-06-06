package com.app.myapp.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.myapp.model.User;
import com.app.myapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Convert roles to GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles() == null
                ? List.of() // or Collections.emptyList()
                : user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())) // Assuming RoleName is an enum
                        .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                // .roles(authorities)
                .authorities(authorities) // Set authorities instead of roles
                .accountExpired(false) // Set as needed
                .accountLocked(false) // Set as needed
                .credentialsExpired(false) // Set as needed
                .disabled(false) // Set as needed
                .build();
    }
}
