package com.example.gamegenerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gamegenerator.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
}
