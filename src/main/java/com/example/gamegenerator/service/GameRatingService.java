package com.example.gamegenerator.service;

import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.gamegenerator.dto.GameRatingRequest;
import com.example.gamegenerator.dto.GameRatingResponse;
import com.example.gamegenerator.entity.GameIdea;
import com.example.gamegenerator.entity.GameRating;
import com.example.gamegenerator.repository.GameRatingRepository;
import com.example.gamegenerator.repository.GameRepository;

@Service
public class GameRatingService {
    
    private final GameRepository gameRepository;

    private final GameRatingRepository gameRatingRepository;

    public GameRatingService(GameRepository gameRepository, GameRatingRepository gameRatingRepository) {
        this.gameRepository = gameRepository;
        this.gameRatingRepository = gameRatingRepository;
    }

    /**
     * Find the score for a given game idea in percentage
     * 
     * 1. Find the sum of all scores for a given game idea.
     * 2. Find the number of ratings for a given game idea.
     * 3. Divide the sum by the number of ratings and multiply by 100.
     */
    public GameRatingResponse getTotalScoreInPercentage(Long gameIdeaId) {
        double sum = gameRatingRepository.sumScoreByGameIdeaId(gameIdeaId);
        double max = gameRatingRepository.countByGameIdeaId(gameIdeaId) * GameRating.MAX_SCORE;
        double total = (sum / max) * 100.0;

        return new GameRatingResponse(gameIdeaId, total);
    }

    /**
     * Create a new game rating
     */
    public GameRatingResponse rateGame(GameRatingRequest gameRatingRequest) {
        Optional<GameIdea> gameIdea = gameRepository.findById(gameRatingRequest.getGameIdeaId());
        if (gameIdea.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game idea not found");
        }

        try {
            GameRating gameRating = new GameRating(gameRatingRequest.getScore(), gameIdea.get());
            gameRatingRepository.save(gameRating);
        } catch (ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        
        return getTotalScoreInPercentage(gameIdea.get().getId());
    }
}
