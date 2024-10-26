package com.app.myapp.enums;

public enum RoleName {
    SUPER_ADMIN,
    ADMIN,
    USER;

    public String getRole() {
        return "ROLE_" + name();
    }

    public static String[] getAllRoles() {
        return new String[] { SUPER_ADMIN.getRole(), ADMIN.getRole(), USER.getRole() };
    }

    public static String[] getAdminAndUserRoles() {
        return new String[] { ADMIN.getRole(), USER.getRole(), SUPER_ADMIN.getRole() };
    }

    @Override
    public String toString() {
        return getRole();
    }
}