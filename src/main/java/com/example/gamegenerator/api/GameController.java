package com.example.gamegenerator.api;

import com.example.gamegenerator.dto.GameInfoResponse;
import com.example.gamegenerator.entity.GameInfo;
import com.example.gamegenerator.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gameidea")
public class GameController {
    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/create")
    public GameInfoResponse createGame() {
        return gameService.createGameInfo();
    }

    @GetMapping("/get/{id}")
    public GameInfoResponse getGame(@PathVariable Long id) {
        return gameService.getGameInfo(id);
    }

    @GetMapping("/get-all")
    public List<GameInfoResponse> getAllGames() {
        return gameService.getAllGameInfo();
    }
}
