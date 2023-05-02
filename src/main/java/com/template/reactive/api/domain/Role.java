package com.template.reactive.api.domain;

public enum Role {
    ADMIN, USER;

    public static Role getRole(String role) {
        if (ADMIN.name().equalsIgnoreCase(role)) { return ADMIN; }
        else if (USER.name().equalsIgnoreCase(role)) { return USER; }
        else { return null; }
    }
}