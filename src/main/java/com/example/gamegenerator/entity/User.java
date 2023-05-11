package com.example.gamegenerator.entity;

import java.util.List;

import com.example.gamegenerator.security.entity.UserWithRoles;
import com.example.gamegenerator.security.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class User extends UserWithRoles {
    
    private int credits;

    public User(String username, String password, int credits, List<Role> roles) {
        super(username, password, roles);
        this.credits = credits;
    }
}
