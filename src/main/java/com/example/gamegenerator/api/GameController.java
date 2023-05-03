package com.example.gamegenerator.api;

import com.example.gamegenerator.dto.GameIdeaCreateRequest;
import com.example.gamegenerator.dto.GameIdeaGenerateRequest;
import com.example.gamegenerator.dto.GameIdeaResponse;
import com.example.gamegenerator.entity.User;
import com.example.gamegenerator.repository.UserRepository;
import com.example.gamegenerator.service.GameService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gameidea")
public class GameController {
    private final GameService gameService;
    private final UserRepository userRepository;

    public GameController(GameService gameService, UserRepository userRepository) {
        this.gameService = gameService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/create/generated")
    public GameIdeaResponse createGeneratedGame(@AuthenticationPrincipal Jwt jwt, @RequestBody GameIdeaGenerateRequest gameIdeaGenerateRequest) {
        return gameService.createGeneratedGameInfo(jwt, gameIdeaGenerateRequest);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/create/user")
    public GameIdeaResponse createGame(@AuthenticationPrincipal Jwt jwt, @RequestBody GameIdeaCreateRequest gameIdeaCreateRequest) {
        return gameService.createGameInfo(jwt, gameIdeaCreateRequest);
    }

    @GetMapping("/public/get/{id}")
    public GameIdeaResponse getGame(@PathVariable Long id) {
        return gameService.getGameInfo(id);
    }

    @GetMapping("/public/get-all")
    public List<GameIdeaResponse> getAllGames(Pageable pageable) {
        return gameService.getAllGameInfo(pageable);
    }

    @GetMapping("/public/genre/{genre}")
    public List<GameIdeaResponse> getGamesByGenre(@PathVariable String genre, Pageable pageable) {

        return gameService.getAllGameInfoByGenre(genre, pageable);
    }

    @GetMapping("/public/count")
    public long getTotalNumber() {
        return gameService.getCount();
    }
}
