package com.app.myapp.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.myapp.model.User;
import com.app.myapp.repository.UserRepository;
import com.app.myapp.util.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException(id + ": User Id not found");
        }

        // Convert roles to GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles() == null
                ? List.of() // or Collections.emptyList()
                : user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())) // Assuming RoleName is an enum
                        .collect(Collectors.toList());

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }
}
