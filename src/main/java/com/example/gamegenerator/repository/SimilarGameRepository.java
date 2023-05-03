package com.example.gamegenerator.repository;

import com.example.gamegenerator.entity.SimilarGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimilarGameRepository extends JpaRepository<SimilarGame, Long> {
}
