package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.GameResponse;
import com.example.gamegenerator.dto.ImageRequest;
import com.example.gamegenerator.dto.OpenApiResponse;
import com.example.gamegenerator.dto.SimilarGamesResponse;
import com.example.gamegenerator.entity.GameInfo;
import com.example.gamegenerator.repository.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final WebClient client = WebClient.create();
    String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    @Value("${app.api-key}")
    private String OPENAI_API_KEY;
    private final String IMAGE_API_URL = "https://api-inference.huggingface.co/models/runwayml/stable-diffusion-v1-5";
    @Value("${app.api-key-image}")
    private String IMAGE_API_KEY;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameInfo getGameInfo(Long id) {
        return gameRepository.findById(id).orElse(null);
    }
    public List<GameInfo> getAllGameInfo() {
        return gameRepository.findAll();
    }

    public GameInfo createGameInfo() {
        GameResponse gameResponse = getGameFromApi();
        GameInfo gameInfo = new GameInfo();
        gameInfo.setTitle(gameResponse.getTitle());
        gameInfo.setDescription(gameResponse.getDescription());
        gameInfo.setGenre(gameResponse.getGenre());
        gameInfo.setPlayer(gameResponse.getPlayer());

        Mono<byte[]> imageMono = createImage(gameResponse);
        Mono<SimilarGamesResponse> similarGamesResponseMono = getSimilarGamesFromApi(gameResponse);

        Mono<GameInfo> gameInfoMono = Mono.zip(imageMono, similarGamesResponseMono)
            .map(tuple -> {
                byte[] image = tuple.getT1();
                SimilarGamesResponse similarGamesResponse = tuple.getT2();

                gameInfo.setImage(image);
                gameInfo.setTitles(similarGamesResponse.getTitles());
                gameInfo.setDescriptions(similarGamesResponse.getDescriptions());
                gameInfo.setGenres(similarGamesResponse.getGenres());
                gameInfo.setPlayers(similarGamesResponse.getPlayers());
                gameInfo.setImages(similarGamesResponse.getImages());
                gameInfo.setLinks(similarGamesResponse.getLinks());

                return gameInfo;
            });
        GameInfo game = gameInfoMono.block();
        if (game != null) {
            game = gameRepository.save(game);
        }
        System.out.println("finished zipping");
        return game;
    }


    public GameResponse getGameFromApi() {
        System.out.println(LocalDateTime.now() + " getGameFromApi() called");

        String GET_GAME_FIXED_PROMPT = "Give me a random unique video game idea. Use the following form for the answer, where player type is what the player is playing as:\n" +
                "Title: \n" +
                "Description: \n" +
                "Player type: \n" +
                "Genre:";

        OpenApiResponse response = getOpenAiApiResponse(GET_GAME_FIXED_PROMPT).block();;

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

    public Mono<SimilarGamesResponse> getSimilarGamesFromApi(GameResponse gameResponse) {
        System.out.println(LocalDateTime.now() + " getSimilarGamesFromApi() called");

        String GET_SIMILAR_GAMES_FIXED_PROMPT = "Give me five similar games from steam from this information:\n" +
            "Title: " + gameResponse.getTitle() + " \n" +
            "Description: " + gameResponse.getDescription() + " \n" +
            "Player type: " + gameResponse.getPlayer() + " \n" +
            "Genre: " + gameResponse.getGenre() + " \n" +
            "Use the following form for the answers, make sure you give the links and images to the games on steam and replace #1 with the game number and where player type is what the player is playing as:\n" +
            "#1 Title: \n" +
            "#1 Description: \n" +
            "#1 Player type: \n" +
            "#1 Genre: \n" +
            "#1 Image: <Give the OPENAI_URL from steam image> \n" +
            "#1 Link: <Give the OPENAI_URL from steam>";

        return getOpenAiApiResponse(GET_SIMILAR_GAMES_FIXED_PROMPT)
            .map(response -> {
                String similarGames = response.choices.get(0).message.getContent();

                List<String> titles = new ArrayList<>();
                List<String> descriptions = new ArrayList<>();
                List<String> playerTypes = new ArrayList<>();
                List<String> genres = new ArrayList<>();
                List<String> images = new ArrayList<>();
                List<String> links = new ArrayList<>();

                String[] games = similarGames.split("\n\n");

                for (String game : games) {
                    String[] info = game.split("\n");
                    titles.add(info[0].replaceAll("(?m)\\s*#\\d+\\s*Title:\\s*(.*)", "$1"));
                    descriptions.add(info[1].replaceAll("(?m)\\s*#\\d+\\s*Description:\\s*(.*)", "$1"));
                    playerTypes.add(info[2].replaceAll("(?m)\\s*#\\d+\\s*Player type:\\s*(.*)", "$1"));
                    genres.add(info[3].replaceAll("(?m)\\s*#\\d+\\s*Genre:\\s*(.*)", "$1"));
                    images.add(info[4].replaceAll("(?m)\\s*#\\d+\\s*Image:\\s*(.*)", "$1"));
                    links.add(info[5].replaceAll("(?m)\\s*#\\d+\\s*Link:\\s*(.*)", "$1"));
                }
                return new SimilarGamesResponse(titles, descriptions, genres, playerTypes, images, links);
            });
    }

    private Mono<OpenApiResponse> getOpenAiApiResponse(String prompt) {

        Map<String, Object> body = new HashMap<>();

        body.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        body.put("messages", messages);
        body.put("temperature", 1);

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

    public Mono<byte[]> createImage(GameResponse gameResponse){
        System.out.println(LocalDateTime.now() + " createImage() called");

        String FIXED_IMAGE_PROMPT = "Give me a picture of a cover for a video game that has the following information, where player type is what the player is playing as:\n" +
            "Title: " + gameResponse.getTitle() + " \n" +
            "Description: " + gameResponse.getDescription() + " \n" +
            "Player type: " + gameResponse.getPlayer() + " \n" +
            "Genre: " + gameResponse.getGenre();

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
