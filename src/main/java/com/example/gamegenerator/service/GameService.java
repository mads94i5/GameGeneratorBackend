package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.*;
import com.example.gamegenerator.entity.GameIdea;
import com.example.gamegenerator.entity.GameRating;
import com.example.gamegenerator.entity.SimilarGame;
import com.example.gamegenerator.entity.User;
import com.example.gamegenerator.repository.GameIdeaRepository;
import com.example.gamegenerator.repository.SimilarGameRepository;
import com.example.gamegenerator.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
  private final GameIdeaRepository gameIdeaRepository;
  private final UserRepository userRepository;
  private final SimilarGameRepository similarGameRepository;

  private final ApiService apiService;
  private final WebClient client = WebClient.create();


  public GameService(GameIdeaRepository gameIdeaRepository,
                     UserRepository userRepository,
                     SimilarGameRepository similarGameRepository,
                     ApiService apiService) {
    this.gameIdeaRepository = gameIdeaRepository;
    this.userRepository = userRepository;
    this.similarGameRepository = similarGameRepository;
    this.apiService = apiService;
  }

  public GameIdeaResponse getGameInfo(Long id) {
    GameIdeaResponse gameIdeaResponse = new GameIdeaResponse();
    GameIdea gameIdea = gameIdeaRepository.findById(id).orElse(null);
    if (gameIdea == null) {
      return null;
    }
    double rating = gameIdeaRepository.getPercentageOfTotalScoreForGameIdea(id, GameRating.MAX_SCORE).orElse(0.0);
    gameIdeaResponse.convert(gameIdea, rating);
    return gameIdeaResponse;
  }

  public List<GameIdeaResponse> getAllGameInfo(Pageable pageable) {
    Page<GameIdea> gameIdeaPage = gameIdeaRepository.findAll(pageable);
    List<GameIdea> gameIdeaList = gameIdeaPage.getContent();
    return gameIdeaList.stream()
        .map(g -> new GameIdeaResponse().convert(g, gameIdeaRepository.getPercentageOfTotalScoreForGameIdea(g.getId(), GameRating.MAX_SCORE).orElse(0.0)))
        .collect(Collectors.toList());
  }

  public List<GameIdeaResponse> getAllGameInfoByGenre(String genre, Pageable pageable) {
    Page<GameIdea> gameIdeaPage = gameIdeaRepository.findGameInfosByGenreContaining(genre, pageable);
    List<GameIdea> gameIdeaList = gameIdeaPage.getContent();

    return gameIdeaList.stream()
        .map(g -> new GameIdeaResponse().convert(g, gameIdeaRepository.getPercentageOfTotalScoreForGameIdea(g.getId(), GameRating.MAX_SCORE).orElse(0.0)))
        .collect(Collectors.toList());
  }

  public GameIdeaResponse createGameInfo(Jwt jwt, GameIdeaCreateRequest gameIdeaCreateRequest) {
    User user = checkUserCredits(jwt);
    gameIdeaCreateRequest.setUserId(user.getUsername());

    GameIdeaResponse gameIdeaResponse = new GameIdeaResponse();

    GameIdea gameIdea = new GameIdea();
    gameIdea.setTitle(gameIdeaCreateRequest.getTitle());
    gameIdea.setDescription(gameIdeaCreateRequest.getDescription());
    gameIdea.setGenre(gameIdeaCreateRequest.getGenre());
    gameIdea.setPlayer(gameIdeaCreateRequest.getPlayer());
    gameIdea.setGenerated(false);
    gameIdea.setUser(userRepository.findById(gameIdeaCreateRequest.getUserId()).orElse(null));

    GameIdea game = getImageAndSimilarGames(gameIdeaCreateRequest, gameIdea).block();
    if (game == null) {
      return null;
    }
    similarGameRepository.saveAll(game.getSimilarGames());
    game = gameIdeaRepository.save(game);
    gameIdeaResponse.convert(game, 0.0);
    return gameIdeaResponse;
  }

  public GameIdeaResponse createGeneratedGameInfo(Jwt jwt, GameIdeaGenerateRequest gameIdeaGenerateRequest) {
    User user = checkUserCredits(jwt);
    gameIdeaGenerateRequest.setUserId(user.getUsername());

    GameIdeaResponse gameIdeaResponse = new GameIdeaResponse();

    GameResponse gameResponse = getGameFromApi();
    GameIdea gameIdea = new GameIdea();
    gameIdea.setTitle(gameResponse.getTitle());
    gameIdea.setDescription(gameResponse.getDescription());
    gameIdea.setGenre(gameResponse.getGenre());
    gameIdea.setPlayer(gameResponse.getPlayer());
    gameIdea.setGenerated(true);
    gameIdea.setUser(userRepository.findById(gameIdeaGenerateRequest.getUserId()).orElse(null));

    GameIdeaCreateRequest gameIdeaCreateRequest = new GameIdeaCreateRequest();
    gameIdeaCreateRequest.setTitle(gameResponse.getTitle());
    gameIdeaCreateRequest.setDescription(gameResponse.getDescription());
    gameIdeaCreateRequest.setGenre(gameResponse.getGenre());
    gameIdeaCreateRequest.setPlayer(gameResponse.getPlayer());

    GameIdea game = getImageAndSimilarGames(gameIdeaCreateRequest, gameIdea).block();
    if (game == null) {
      return null;
    }
    similarGameRepository.saveAll(game.getSimilarGames());
    game = gameIdeaRepository.save(game);
    gameIdeaResponse.convert(game, 0.0);
    return gameIdeaResponse;
  }

  public Mono<GameIdea> getImageAndSimilarGames(GameIdeaCreateRequest gameRequest, GameIdea gameIdea) {
    Mono<byte[]> imageMono = createImage(gameRequest);
    Mono<SimilarGamesResponse> similarGamesResponseMono = getSimilarGamesFromApi(gameRequest);

    return Mono.zip(imageMono, similarGamesResponseMono)
        .map(tuple -> {
          byte[] image = tuple.getT1();
          SimilarGamesResponse similarGamesResponse = tuple.getT2();

          gameIdea.setImage(image);
          gameIdea.setSimilarGames(similarGamesResponse.getSimilarGames());

          return gameIdea;
        });
  }

  public GameResponse getGameFromApi() {
    System.out.println(LocalDateTime.now() + " getGameFromApi() called");

    String GET_GAME_FIXED_PROMPT = "Give me a random unique video game idea. Use the following form for the answer, where player type is what the player is playing as:\n" +
        "Title: \n" +
        "Description: \n" +
        "Player type: \n" +
        "Genre:";

    OpenApiResponse response = apiService.getOpenAiApiResponse(GET_GAME_FIXED_PROMPT, 1.3).block();

    String game = response.choices.get(0).message.getContent();

    String[] gameResponseLines = game.split("\\r?\\n");

    String title = "";
    String description = "";
    String playerType = "";
    String genre = "";

    for (String line : gameResponseLines) {
      if (line.startsWith("Title:")) {
        title = line.substring(7);
      } else if (line.startsWith("Description:")) {
        description = line.substring(13);
      } else if (line.startsWith("Player type:")) {
        playerType = line.substring(13);
      } else if (line.startsWith("Genre:")) {
        genre = line.substring(7);
      }
    }

    System.out.println(title);
    System.out.println(description);
    System.out.println(playerType);
    System.out.println(genre);

    return new GameResponse(title, description, genre, playerType);
  }

  public Mono<SimilarGamesResponse> getSimilarGamesFromApi(GameIdeaCreateRequest gameRequest) {
    System.out.println(LocalDateTime.now() + " getSimilarGamesFromApi() called");

    String GET_SIMILAR_GAMES_FIXED_PROMPT = "Give me five similar games from thee video game platform Steam from the following information:\n" +
        "Title: " + gameRequest.getTitle() + " \n" +
        "Description: " + gameRequest.getDescription() + " \n" +
        "Player type: " + gameRequest.getPlayer() + " \n" +
        "Genre: " + gameRequest.getGenre() + " \n" +
        "Use the following form for the answers, make sure you give the links and images to the games on steam and replace #1 with the game number and where player type is what the player is playing as:\n" +
        "#1 Title: \n" +
        "#1 Description: \n" +
        "#1 Player type: \n" +
        "#1 Genre: \n" +
        "#1 Image: <Give the URL from steam image> \n" +
        "#1 Link: <Give the URL from steam>";

    return apiService.getOpenAiApiResponse(GET_SIMILAR_GAMES_FIXED_PROMPT, 0)
        .map(response -> {
          String similarGames = response.choices.get(0).message.getContent();

          List<SimilarGame> similarGamesList = new ArrayList<>();

          String[] games = similarGames.split("\n\n");

          for (String game : games) {
            String[] info = game.split("\n");
            String title = info[0].replaceAll("(?m)\\s*#\\d+\\s*Title:\\s*(.*)", "$1");
            String description = info[1].replaceAll("(?m)\\s*#\\d+\\s*Description:\\s*(.*)", "$1");
            String playerType = info[2].replaceAll("(?m)\\s*#\\d+\\s*Player type:\\s*(.*)", "$1");
            String genre = info[3].replaceAll("(?m)\\s*#\\d+\\s*Genre:\\s*(.*)", "$1");
            String image = info[4].replaceAll("(?m)\\s*#\\d+\\s*Image:\\s*(.*)", "$1");
            String link = info[5].replaceAll("(?m)\\s*#\\d+\\s*Link:\\s*(.*)", "$1");

            SimilarGame similarGame = new SimilarGame(title, description, playerType, genre, image, link);
            similarGamesList.add(similarGame);
          }

          return new SimilarGamesResponse(similarGamesList);
        });
  }

  public Mono<byte[]> createImage(GameIdeaCreateRequest gameRequest) {
    System.out.println(LocalDateTime.now() + " createImage() called");

    String FIXED_IMAGE_PROMPT = "Give me a picture of a cover for a video game that has the following information, where player type is what the player is playing as:\n" +
        "Title: " + gameRequest.getTitle() + " \n" +
        "Description: " + gameRequest.getDescription() + " \n" +
        "Player type: " + gameRequest.getPlayer() + " \n" +
        "Genre: " + gameRequest.getGenre();

    return apiService.generateImageResponse(FIXED_IMAGE_PROMPT);
  }


  public Long getCount() {
    return gameIdeaRepository.count();
  }

  private User checkUserCredits(Jwt jwt) {
    Optional<User> optionalUser = userRepository.findById(jwt.getSubject());
    if (optionalUser.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    User user = optionalUser.get();
    if (user.getCredits() < 1) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not enough credits");
    }
    user.setCredits(user.getCredits() - 1);
    userRepository.save(user);
    return user;
  }


}
