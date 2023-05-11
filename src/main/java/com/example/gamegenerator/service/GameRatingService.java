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
import com.example.gamegenerator.repository.GameIdeaRepository;

@Service
public class GameRatingService {
    
    private final GameIdeaRepository gameIdeaRepository;

    private final GameRatingRepository gameRatingRepository;

    public GameRatingService(GameIdeaRepository gameIdeaRepository, GameRatingRepository gameRatingRepository) {
        this.gameIdeaRepository = gameIdeaRepository;
        this.gameRatingRepository = gameRatingRepository;
    }

    /**
     * Create a new game rating
     */
    public GameRatingResponse rateGame(GameRatingRequest gameRatingRequest) {
        Optional<GameIdea> gameIdea = gameIdeaRepository.findById(gameRatingRequest.getGameIdeaId());
        if (gameIdea.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game idea not found");
        }

        try {
            GameRating gameRating = new GameRating(gameRatingRequest.getScore(), gameIdea.get());
            gameRatingRepository.save(gameRating);
        } catch (ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        
        double rating = gameIdeaRepository.getPercentageOfTotalScoreForGameIdea(gameIdea.get().getId(), GameRating.MAX_SCORE).orElse(0.0);
        int numberOfRatings = gameIdeaRepository.getNumberOfRatingsForGameIdea(gameIdea.get().getId());
        return new GameRatingResponse(gameIdea.get().getId(), rating, numberOfRatings);
    }
}
