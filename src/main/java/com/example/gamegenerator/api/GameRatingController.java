package com.example.gamegenerator.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.gamegenerator.dto.GameRatingRequest;
import com.example.gamegenerator.dto.GameRatingResponse;
import com.example.gamegenerator.service.GameRatingService;

@RestController
@RequestMapping("/api/game-ratings")
public class GameRatingController {
    
    private GameRatingService gameRatingService;

    public GameRatingController(GameRatingService gameRatingService) {
        this.gameRatingService = gameRatingService;
    }

    @PostMapping
    public GameRatingResponse rateGame(@RequestBody GameRatingRequest gameRatingRequest) {
        return gameRatingService.rateGame(gameRatingRequest);
    }
}
