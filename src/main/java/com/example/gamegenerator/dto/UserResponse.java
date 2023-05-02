package com.example.gamegenerator.dto;

import java.util.List;

import com.example.gamegenerator.entity.User;
import com.example.gamegenerator.security.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String username;
    private List<Role> roles;
    private int credits;

    public UserResponse(User user) {
        this.username = user.getUsername();
        this.credits = user.getCredits();
        this.roles = user.getRoles();
    }
}
