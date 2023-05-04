package com.example.gamegenerator.repository;

import com.example.gamegenerator.entity.GameCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameCodeRepository extends JpaRepository<GameCode, Long> {
}
