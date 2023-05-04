package com.example.gamegenerator.config;

import com.example.gamegenerator.dto.GameIdeaCreateRequest;
import com.example.gamegenerator.entity.GameIdea;
import com.example.gamegenerator.repository.GameRepository;
import com.example.gamegenerator.service.GameService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DevConfig implements ApplicationRunner {

  @Value("${app.env}")
  private String environment;

  private GameRepository gameRepository;

  public DevConfig(GameRepository gameRepository) {
    this.gameRepository = gameRepository;
  }

  // runner method here
  public void testData() {


    if (environment.equals("development")) {
      // Do something only in dev environment

      /**/

      GameIdea game1 = GameIdea.builder()
          .title("Super Mario Bros.")
          .genre("Platformer")
          .description("Classic platformer game featuring Mario and Luigi")
          .player("Single player")
          .build();
      GameIdea game2 = GameIdea.builder()
          .title("The Legend of Zelda: Breath of the Wild")
          .genre("Action-adventure")
          .description("Open-world adventure game with puzzles and combat")
          .player("Single player")
          .build();
      GameIdea game3 = GameIdea.builder()
          .title("Overwatch")
          .genre("First-person shooter")
          .description("Multiplayer team-based shooter with unique heroes")
          .player("Multiplayer")
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

      gameRepository.save(game1);
      gameRepository.save(game2);
      gameRepository.save(game3);
      gameRepository.save(game4);
      gameRepository.save(game5);
      gameRepository.save(game6);
      gameRepository.save(game7);
      gameRepository.save(game8);
      gameRepository.save(game9);
      gameRepository.save(game10);
      gameRepository.save(game11);
      gameRepository.save(game12);
      gameRepository.save(game13);
      gameRepository.save(game14);

    }
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {

    testData();
  }
}
