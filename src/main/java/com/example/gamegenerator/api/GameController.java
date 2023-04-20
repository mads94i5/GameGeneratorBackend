package com.example.gamegenerator.api;

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
    public GameInfo createGame() {
        GameInfo gameInfo = gameService.createGameInfo();
        System.out.println(gameInfo.getTitle());
        return gameInfo;
    }

    @GetMapping("/get/{id}")
    public GameInfo getGame(@PathVariable Long id) {
        return gameService.getGameInfo(id);
    }

    @GetMapping("/get-all")
    public List<GameInfo> getAllGames() {
        return gameService.getAllGameInfo();
    }
}
