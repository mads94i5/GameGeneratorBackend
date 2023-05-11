package com.example.gamegenerator.security.entity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.gamegenerator.security.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")
public class UserWithRoles implements UserDetails {
    
    @Id
    private String username;
    private String password;

    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;

    private List<Role> roles;

    public UserWithRoles(String username, String password, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Role.toStringList(roles)
            .stream()
            .map(s -> new SimpleGrantedAuthority("ROLE_" + s))
            .collect(Collectors.toList());
    }
}
