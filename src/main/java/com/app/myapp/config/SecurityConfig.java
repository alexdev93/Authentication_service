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
import com.app.myapp.exception.CustomAccessDeniedHandler;
import com.app.myapp.exception.CustomAuthenticationEntryPoint;
import com.app.myapp.filter.JwtRequestFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true) // This enables method-based access control
public class SecurityConfig {

        public static final String AUTHENTICATE_ENDPOINT = "/auth/**";

        private final CustomUserDetailsService userDetailsService;
        private final JwtRequestFilter jwtRequestFilter;
        private final CustomAuthenticationEntryPoint authEntryPoint;
        private final CustomAccessDeniedHandler accessDeniedHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(AUTHENTICATE_ENDPOINT).permitAll()
                                                .requestMatchers("/users/**")
                                                .hasAnyAuthority(RoleName.ADMIN.name(), RoleName.USER.name())
                                                .requestMatchers("/roles/**").hasAuthority(RoleName.ADMIN.name())
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling(handling -> handling
                                                .authenticationEntryPoint(authEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler));

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
