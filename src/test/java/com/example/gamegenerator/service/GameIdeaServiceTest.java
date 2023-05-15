package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.GameIdeaResponse;
import com.example.gamegenerator.entity.GameIdea;
import com.example.gamegenerator.repository.GameIdeaRepository;
import com.example.gamegenerator.repository.SimilarGameRepository;
import com.example.gamegenerator.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GameIdeaServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SimilarGameRepository similarGameRepository;
    @Autowired
    private GameIdeaRepository gameIdeaRepository;
    private GameIdeaService gameIdeaService;
    private UserService userService;
    private ApiService apiService;
    GameIdea gameIdea1;
    GameIdea gameIdea2;

    @BeforeEach
    void beforeEach() {
        gameIdeaService = new GameIdeaService(gameIdeaRepository, userRepository, userService, similarGameRepository, apiService);
        userService = new UserService(userRepository);
        apiService = new ApiService();
        gameIdea1 = GameIdea.builder()
                .id(1L)
                .title("Game1")
                .description("Description1")
                .player("Player1")
                .genre("Genre1")
                .build();
        gameIdea2 = GameIdea.builder()
                .id(2L)
                .title("Game2")
                .description("Description2")
                .player("Player2")
                .genre("Genre2")
                .build();

        gameIdeaRepository.save(gameIdea1);
        gameIdeaRepository.save(gameIdea2);
    }

    @Test
    public void testGetGameInfo_ExistingId() {
        // Arrange
        Long gameId = 3L;
        GameIdea gameIdea = new GameIdea();
        gameIdea.setId(gameId);
        gameIdeaRepository.save(gameIdea);

        // Act
        GameIdeaResponse result = gameIdeaService.getGameInfo(gameId);

        // Assert
        assertNotNull(result);
        assertEquals(gameId, result.getId());
    }

    @Test
    public void testGetGameInfo_NonexistentId() {
        // Arrange
        Long gameId = 456L;

        // Act
        GameIdeaResponse result = gameIdeaService.getGameInfo(gameId);

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetAllGameInfo() {
        // Act
        List<GameIdeaResponse> result = gameIdeaService.getAllGameInfo(PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        GameIdeaResponse game1Response = result.get(0);
        assertEquals(5L, game1Response.getId());
        assertEquals("Game1", game1Response.getTitle());

        GameIdeaResponse game2Response = result.get(1);
        assertEquals(6L, game2Response.getId());
        assertEquals("Game2", game2Response.getTitle());
    }

    @Test
    public void testGetCount() {
        // Act
        Long count = gameIdeaService.getCount();

        // Assert
        assertNotNull(count);
        assertEquals(2L, count.longValue());
    }
}