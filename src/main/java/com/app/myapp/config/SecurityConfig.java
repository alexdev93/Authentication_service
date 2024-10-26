package com.app.myapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.myapp.auth.CustomUserDetailsService;
import com.app.myapp.enums.RoleName;
import com.app.myapp.exception.AuthEntryPoint;
import com.app.myapp.filter.JwtRequestFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        public static final String AUTHENTICATE_ENDPOINT = "/auth/**";
        public static final String[] SECURE_ENDPOINTS = { "/users/**", "/items/**", "/roles/**" };

        private final CustomUserDetailsService userDetailsService;
        private final JwtRequestFilter jwtRequestFilter;
        private final AuthEntryPoint authEntryPoint;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(AUTHENTICATE_ENDPOINT).permitAll()
                                                // .requestMatchers("/**").hasRole(RoleName.SUPER_ADMIN.name())
                                                .requestMatchers(SECURE_ENDPOINTS).authenticated()
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling(handling -> handling
                                                .authenticationEntryPoint(authEntryPoint));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder
                                .userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder());
                return authenticationManagerBuilder.build();
        }
}
