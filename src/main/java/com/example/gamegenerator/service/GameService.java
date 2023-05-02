package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.*;
import com.example.gamegenerator.entity.GameIdea;
import com.example.gamegenerator.entity.GameMechanic;
import com.example.gamegenerator.entity.SimilarGame;
import com.example.gamegenerator.repository.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final WebClient client = WebClient.create();
    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    @Value("${app.api-key}")
    private String OPENAI_API_KEY;
    private final String IMAGE_API_URL = "https://api-inference.huggingface.co/models/runwayml/stable-diffusion-v1-5";
    // private final String IMAGE_API_URL = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-v2-1";
    @Value("${app.api-key-image}")
    private String IMAGE_API_KEY;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameIdeaResponse getGameInfo(Long id) {
        GameIdeaResponse gameIdeaResponse = new GameIdeaResponse();
        GameIdea gameIdea = gameRepository.findById(id).orElse(null);
        if (gameIdea == null) {
            return null;
        }
        gameIdeaResponse.convert(gameIdea);
        return gameIdeaResponse;
    }
    public List<GameIdeaResponse> getAllGameInfo() {
        List<GameIdea> gameIdeaList = gameRepository.findAll();
        List<GameIdeaResponse> gameIdeaRespons = gameIdeaList.stream()
                .map(gameIdea -> new GameIdeaResponse().convert(gameIdea))
                .collect(Collectors.toList());
        return gameIdeaRespons;
    }
    public GameIdeaResponse createGameInfo(GameIdeaCreateRequest gameIdeaCreateRequest) {
        GameIdeaResponse gameIdeaResponse = new GameIdeaResponse();

        GameIdea gameIdea = new GameIdea();
        gameIdea.setTitle(gameIdeaCreateRequest.getTitle());
        gameIdea.setDescription(gameIdeaCreateRequest.getDescription());
        gameIdea.setGenre(gameIdeaCreateRequest.getGenre());
        gameIdea.setPlayer(gameIdeaCreateRequest.getPlayer());
        gameIdea.setGameMechanics(gameIdeaCreateRequest.getGameMechanics());
        gameIdea.setGenerated(false);

        GameIdea game = getImageAndSimilarGames(gameIdeaCreateRequest, gameIdea).block();
        if (game == null) { return null; }
        game = gameRepository.save(game);
        gameIdeaResponse.convert(game);
        return gameIdeaResponse;
    }
    public GameIdeaResponse createGeneratedGameInfo(GameIdeaGenerateRequest gameIdeaGenerateRequest) {
        GameIdeaResponse gameIdeaResponse = new GameIdeaResponse();

        GameResponse gameResponse = getGameFromApi(gameIdeaGenerateRequest.getNumberOfMechanics());
        GameIdea gameIdea = new GameIdea();
        gameIdea.setTitle(gameResponse.getTitle());
        gameIdea.setDescription(gameResponse.getDescription());
        gameIdea.setGenre(gameResponse.getGenre());
        gameIdea.setPlayer(gameResponse.getPlayer());
        gameIdea.setGameMechanics(gameResponse.getGameMechanics());
        gameIdea.setGenerated(true);

        GameIdeaCreateRequest gameIdeaCreateRequest = new GameIdeaCreateRequest();
        gameIdeaCreateRequest.setTitle(gameResponse.getTitle());
        gameIdeaCreateRequest.setDescription(gameResponse.getDescription());
        gameIdeaCreateRequest.setGenre(gameResponse.getGenre());
        gameIdeaCreateRequest.setPlayer(gameResponse.getPlayer());
        gameIdeaCreateRequest.setGameMechanics(gameResponse.getGameMechanics());

        GameIdea game = getImageAndSimilarGames(gameIdeaCreateRequest, gameIdea).block();
        if (game == null) { return null; }
        game = gameRepository.save(game);
        gameIdeaResponse.convert(game);
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
    public GameResponse getGameFromApi(int numberOfMechanics) {
        System.out.println(LocalDateTime.now() + " getGameFromApi() called");

        String GET_GAME_FIXED_PROMPT = "Give me a random unique video game idea. Use the following form for the answer, where player type is what the player is playing as and game mechanics are " + numberOfMechanics + " game mechanics, seperated by semicolons:\n" +
                "Title: \n" +
                "Description: \n" +
                "Player type: \n" +
                "Genre: \n" +
                "Game mechanics:";

        OpenApiResponse response = getOpenAiApiResponse(GET_GAME_FIXED_PROMPT, 1.3).block();;

        String game = response.choices.get(0).message.getContent();

        String[] gameResponseLines = game.split("\\r?\\n");

        String title = "";
        String description = "";
        String playerType = "";
        String genre = "";
        List<GameMechanic> gameMechanics = new ArrayList<>();

        for (String line : gameResponseLines) {
            if (line.startsWith("Title:")) {
                title = line.substring(7);
            } else if (line.startsWith("Description:")) {
                description = line.substring(13);
            } else if (line.startsWith("Player type:")) {
                playerType = line.substring(13);
            } else if (line.startsWith("Genre:")) {
                genre = line.substring(7);
            } else if (line.startsWith("Game mechanics:")) {
                gameMechanics = Arrays.stream(line.substring(16).split(";"))
                        .map(GameMechanic::new)
                        .collect(Collectors.toList());
            }
        }

        System.out.println(title);
        System.out.println(description);
        System.out.println(playerType);
        System.out.println(genre);
        for (GameMechanic gameMechanic : gameMechanics) {
            System.out.println(gameMechanic);
        }

        return new GameResponse(title, description, genre, playerType, gameMechanics);
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

        return getOpenAiApiResponse(GET_SIMILAR_GAMES_FIXED_PROMPT, 0)
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
    private Mono<OpenApiResponse> getOpenAiApiResponse(String prompt, double temperature) {

        Map<String, Object> body = new HashMap<>();

        body.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        body.put("messages", messages);
        body.put("temperature", temperature);

        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return client.post()
                .uri(OPENAI_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .retrieve()
                .bodyToMono(OpenApiResponse.class);
    }
    public Mono<byte[]> createImage(GameIdeaCreateRequest gameRequest){
        System.out.println(LocalDateTime.now() + " createImage() called");

        String FIXED_IMAGE_PROMPT = "Give me a picture of a cover for a video game that has the following information, where player type is what the player is playing as:\n" +
            "Title: " + gameRequest.getTitle() + " \n" +
            "Description: " + gameRequest.getDescription() + " \n" +
            "Player type: " + gameRequest.getPlayer() + " \n" +
            "Genre: " + gameRequest.getGenre();

        return generateImage(FIXED_IMAGE_PROMPT);
    }
    public Mono<byte[]> generateImage(String prompt) {
        // Set up request data
        ImageRequest request = new ImageRequest();
        request.setInputs(prompt);

        // Return request to API
        return client.post()
                .uri(IMAGE_API_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + IMAGE_API_KEY)
                .body(Mono.just(request), ImageRequest.class)
                .retrieve()
                .bodyToMono(byte[].class);
    }
}
