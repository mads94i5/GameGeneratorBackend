package com.example.gamegenerator.config;

import com.example.gamegenerator.dto.GameIdeaCreateRequest;
import com.example.gamegenerator.entity.GameIdea;
import com.example.gamegenerator.entity.GameRating;
import com.example.gamegenerator.repository.GameRatingRepository;
import com.example.gamegenerator.repository.GameRepository;
import com.example.gamegenerator.service.GameService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("dev")
public class DevConfig implements ApplicationRunner {

  private GameRepository gameRepository;
  private GameRatingRepository gameRatingRepository;

  public DevConfig(GameRepository gameRepository, GameRatingRepository gameRatingRepository) {
    this.gameRepository = gameRepository;
    this.gameRatingRepository = gameRatingRepository;
  }

  public void testData() {
    // Only add test data the first time the application is run
    // locally by a developer
    if (gameRepository.count() > 0) {
      return;
    }
    GameIdea game1 = GameIdea.builder()
          .title("Super Mario Bros.")
          .genre("Platformer")
          .description("Classic platformer game featuring Mario and Luigi")
          .player("Mario")
          .build();
      GameIdea game2 = GameIdea.builder()
          .title("The Legend of Zelda: Breath of the Wild")
          .genre("Action-adventure")
          .description("Open-world adventure game with puzzles and combat")
          .player("Link")
          .build();
      GameIdea game3 = GameIdea.builder()
          .title("Overwatch")
          .genre("First-person shooter")
          .description("Multiplayer team-based shooter with unique heroes")
          .player("Fantasy characters")
          .build();

      gameRepository.save(game1);
      gameRepository.save(game2);
      gameRepository.save(game3);

      gameRatingRepository.save(new GameRating(3, game1));
      gameRatingRepository.save(new GameRating(4, game1));
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {

    testData();
  }
}
