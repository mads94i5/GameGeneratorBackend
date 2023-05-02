package com.example.gamegenerator.security.enums;

import java.util.List;
import java.util.stream.Collectors;

public enum Role {
    USER,
    ADMIN;

    public static List<String> toStringList(List<Role> roles) {
        return roles.stream().map(Role::name).collect(Collectors.toList());
    }
}
