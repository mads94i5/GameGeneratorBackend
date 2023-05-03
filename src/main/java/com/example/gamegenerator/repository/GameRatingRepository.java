package com.example.gamegenerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

import com.example.gamegenerator.entity.GameRating;

@Validated // Ensure that validation exceptions are thrown if validation annotations are added
public interface GameRatingRepository extends JpaRepository<GameRating, Long> {
    /**
     * Find the sum of all scores for a given game idea
     */
    double sumScoreByGameIdeaId(Long gameIdeaId);

    /**
     * Find the number of ratings for a given game idea
     */
    int countByGameIdeaId(Long gameIdeaId);
}
