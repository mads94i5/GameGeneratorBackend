package com.example.gamegenerator.api;

import com.example.gamegenerator.dto.GameIdeaCreateRequest;
import com.example.gamegenerator.dto.GameIdeaGenerateRequest;
import com.example.gamegenerator.dto.GameIdeaResponse;
import com.example.gamegenerator.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gameidea")
public class GameController {
    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/create/generated")
    public GameIdeaResponse createGeneratedGame(@RequestBody GameIdeaGenerateRequest gameIdeaGenerateRequest) {
        return gameService.createGeneratedGameInfo(gameIdeaGenerateRequest);
    }

    @GetMapping("/create/user")
    public GameIdeaResponse createGame(@RequestBody GameIdeaCreateRequest gameIdeaCreateRequest) {
        return gameService.createGameInfo(gameIdeaCreateRequest);
    }

    @GetMapping("/get/{id}")
    public GameIdeaResponse getGame(@PathVariable Long id) {
        return gameService.getGameInfo(id);
    }

    @GetMapping("/get-all")
    public List<GameIdeaResponse> getAllGames() {
        return gameService.getAllGameInfo();
    }
}
