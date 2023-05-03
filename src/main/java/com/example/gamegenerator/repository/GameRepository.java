package com.example.gamegenerator.repository;

import com.example.gamegenerator.entity.GameIdea;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<GameIdea, Long> {

  Page<GameIdea> findGameInfosByGenreContaining(String genre, Pageable pageable);

  /**
   * Find the score for a given game idea in percentage
   * 
   * 1. Find the sum of all scores for a given game idea.
   * 2. Find the number of ratings for a given game idea.
   * 3. Divide the sum by the number of ratings and multiply by 100.
   * 
   * It returns an Optional<Double> because the game idea might not have any ratings.
   * So if the game idea is not found, it will return an empty Optional instead of throwing an exception.
   */
  @Query("SELECT (SUM(rating.score) / (COUNT(rating) * :maxScore)) * 100.0 " +
           "FROM GameRating rating " +
           "WHERE rating.gameIdea.id = :id")
  Optional<Double> getPercentageOfTotalScoreForGameIdea(@Param("id") Long id,
                                              @Param("maxScore") Double maxScore);

}
