package com.app.myapp.enums;

public enum RoleName {
    SUPER_ADMIN,
    ADMIN,
    USER;

    @Override
    public String toString() {
        return name(); // This will return the name of the enum as a string (e.g., "SUPER_ADMIN")
    }
}