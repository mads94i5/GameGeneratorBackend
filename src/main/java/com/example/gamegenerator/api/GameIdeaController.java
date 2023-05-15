package com.example.gamegenerator.api;

import com.example.gamegenerator.dto.GameIdeaCreateRequest;
import com.example.gamegenerator.dto.GameIdeaGenerateRequest;
import com.example.gamegenerator.dto.GameIdeaResponse;
import com.example.gamegenerator.service.GameIdeaService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gameidea")
public class GameIdeaController {
    private final GameIdeaService gameIdeaService;

    public GameIdeaController(GameIdeaService gameIdeaService) {
        this.gameIdeaService = gameIdeaService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/create/generated")
    public GameIdeaResponse createGeneratedGame(@AuthenticationPrincipal Jwt jwt, @RequestBody GameIdeaGenerateRequest gameIdeaGenerateRequest) {
        return gameIdeaService.createGeneratedGameIdea(jwt, gameIdeaGenerateRequest);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/create/user")
    public GameIdeaResponse createGame(@AuthenticationPrincipal Jwt jwt, @RequestBody GameIdeaCreateRequest gameIdeaCreateRequest) {
        return gameIdeaService.createGameIdea(jwt, gameIdeaCreateRequest);
    }

    @GetMapping("/public/get/{id}")
    public GameIdeaResponse getGame(@PathVariable Long id) {
        return gameIdeaService.getGameInfo(id);
    }

    @GetMapping("/public/get-all")
    public List<GameIdeaResponse> getAllGames(Pageable pageable) {
        return gameIdeaService.getAllGameInfo(pageable);
    }

    @GetMapping("/public/genre/{genre}")
    public List<GameIdeaResponse> getGamesByGenre(@PathVariable String genre, Pageable pageable) {
        return gameIdeaService.getAllGameInfoByGenre(genre, pageable);
    }

    @GetMapping("/public/count")
    public long getTotalNumber() {
        return gameIdeaService.getCount();
    }
}
