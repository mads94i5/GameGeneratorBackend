package com.example.gamegenerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

import com.example.gamegenerator.entity.GameRating;

@Validated // Ensure that validation exceptions are thrown if validation annotations are added
public interface GameRatingRepository extends JpaRepository<GameRating, Long> {
}
