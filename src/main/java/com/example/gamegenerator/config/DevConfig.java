package com.example.gamegenerator.config;

import com.example.gamegenerator.entity.GameIdea;
import com.example.gamegenerator.entity.GameRating;
import com.example.gamegenerator.repository.GameRatingRepository;
import com.example.gamegenerator.repository.GameIdeaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("dev")
public class DevConfig implements ApplicationRunner {

  @Value("${app.env}")
  private String environment;

  private GameIdeaRepository gameIdeaRepository;
  private GameRatingRepository gameRatingRepository;

  public DevConfig(GameIdeaRepository gameIdeaRepository) {
    this.gameIdeaRepository = gameIdeaRepository;
    this.gameRatingRepository = gameRatingRepository;
  }

  // runner method here
  public void testData() {
    // Only add test data the first time the application is run
    // locally by a developer
    if (gameIdeaRepository.count() > 0) {
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

    GameIdea game4 = GameIdea.builder()
        .title("League of Legends")
        .genre("MOBA")
        .description("Multiplayer online battle arena with champions")
        .player("Multiplayer")
        .build();

    GameIdea game5 = GameIdea.builder()
        .title("Final Fantasy VII Remake")
        .genre("JRPG")
        .description("Singleplayer role-playing game with an epic story")
        .player("Singleplayer")
        .build();

    GameIdea game6 = GameIdea.builder()
        .title("Fortnite")
        .genre("Battle royale")
        .description("Multiplayer game where players fight to be the last one standing")
        .player("Multiplayer")
        .build();

    GameIdea game7 = GameIdea.builder()
        .title("Civilization VI")
        .genre("Strategy")
        .description("Singleplayer or multiplayer game where players lead a civilization to victory")
        .player("Singleplayer, multiplayer")
        .build();

    GameIdea game8 = GameIdea.builder()
        .title("The Legend of Zelda: Breath of the Wild")
        .genre("Action-adventure")
        .description("Open-world singleplayer game with exploration and puzzles")
        .player("Singleplayer")
        .build();

    GameIdea game9 = GameIdea.builder()
        .title("FIFA 22")
        .genre("Sports")
        .description("Multiplayer game with football/soccer teams and players")
        .player("Singleplayer, multiplayer")
        .build();

    GameIdea game10 = GameIdea.builder()
        .title("Dark Souls III")
        .genre("Action RPG")
        .description("Singleplayer game with difficult enemies and challenging gameplay")
        .player("Singleplayer")
        .build();

    GameIdea game11 = GameIdea.builder()
        .title("The Legend of Zelda: Breath of the Wild")
        .genre("Action-adventure")
        .description("Open-world adventure game set in a post-apocalyptic world")
        .player("Single player")
        .build();

    GameIdea game12 = GameIdea.builder()
        .title("Fortnite")
        .genre("Battle royale")
        .description("Multiplayer game where players fight to be the last one standing")
        .player("Multiplayer")
        .build();

    GameIdea game13 = GameIdea.builder()
        .title("Minecraft")
        .genre("Sandbox")
        .description("Game about placing blocks to build anything you can imagine")
        .player("Single player, Multiplayer")
        .build();

    GameIdea game14 = GameIdea.builder()
        .title("Final Fantasy VII Remake")
        .genre("Role-playing")
        .description("Story-driven game set in a dystopian world with a focus on combat and exploration")
        .player("Single player")
        .build();

    gameIdeaRepository.save(game1);
    gameIdeaRepository.save(game2);
    gameIdeaRepository.save(game3);
    gameIdeaRepository.save(game4);
    gameIdeaRepository.save(game5);
    gameIdeaRepository.save(game6);
    gameIdeaRepository.save(game7);
    gameIdeaRepository.save(game8);
    gameIdeaRepository.save(game9);
    gameIdeaRepository.save(game10);
    gameIdeaRepository.save(game11);
    gameIdeaRepository.save(game12);
    gameIdeaRepository.save(game13);
    gameIdeaRepository.save(game14);



      gameRatingRepository.save(new GameRating(3, game1));
      gameRatingRepository.save(new GameRating(4, game1));
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {

    testData();
  }
}
