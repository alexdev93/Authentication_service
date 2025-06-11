package com.app.myapp.config;

public class ApiEndpoints {
        public static final String[] PUBLIC_ENDPOINTS = {
                        "/auth/reset-password",
                        "/auth/login",
                        "/auth/forgot-password",
                        "/actuator/**"
        };

        public static final String[] ADMIN_ENDPOINTS = {
                        "/auth/register",
                        "/users",
                        "/users/{userId}/assign-roles",
                        "/roles"
        };

        public static final String[] USER_ENDPOINTS = {
                        "/users/me/",
                        "/users/me/update",
                        "/auth/refresh-token",
        };
}
