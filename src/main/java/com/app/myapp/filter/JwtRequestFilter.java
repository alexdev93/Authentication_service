package com.app.myapp.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.myapp.service.JwtService;
import com.app.myapp.util.CustomUserDetails;
import com.app.myapp.util.Helper;

import org.springframework.lang.NonNull;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtUtil;
    private final UserDetailsService userDetailsService;
    private final Helper helper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String id = null;
        String jwt = null;

        try {
            // Check if the header starts with "Bearer "
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7); // Extract JWT
                id = jwtUtil.extractId(jwt, false); // Extract username
            }

            // If username is valid and not authenticated, set the authentication context
            if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserDetails userDetails = (CustomUserDetails) this.userDetailsService.loadUserByUsername(id);

                // Validate the token
                if (jwtUtil.validateToken(jwt, userDetails.getId(), false)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in context
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

        } catch (ExpiredJwtException ex) {
            helper.writeErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                    ex.getLocalizedMessage());
            return;

        } catch (JwtException ex) {
            helper.writeErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                    ex.getLocalizedMessage());
            return;

        } catch (Exception ex) {
            helper.writeErrorResponse(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Server Error", ex.getLocalizedMessage());
            return;
        }

        chain.doFilter(request, response);
    }
}
