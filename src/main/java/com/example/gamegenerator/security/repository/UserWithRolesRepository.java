package com.example.gamegenerator.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gamegenerator.security.entity.UserWithRoles;

public interface UserWithRolesRepository extends JpaRepository<UserWithRoles, String> {
}
