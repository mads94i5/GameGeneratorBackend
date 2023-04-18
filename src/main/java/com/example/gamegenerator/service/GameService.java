package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.GameResponse;
import com.example.gamegenerator.dto.OpenApiResponse;
import com.example.gamegenerator.dto.SimilarGamesResponse;
import com.example.gamegenerator.entity.GameInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameService {
    WebClient client = WebClient.create();
    @Value("${app.api-key}")
    private String API_KEY;
    String URL = "https://api.openai.com/v1/chat/completions";
    String GET_GAME_FIXED_PROMPT = "Give me a random unique video game idea. Use the following form for the answer, where player type is what the player is playing as:\n" +
            "Title: \n" +
            "Description: \n" +
            "Player type: \n" +
            "Genre:";


    public GameInfo getGameInfo() {
        GameResponse gameResponse = getGame();
        GameInfo gameInfo = new GameInfo();
        gameInfo.setTitle(gameResponse.getTitle());
        gameInfo.setDescription(gameResponse.getDescription());
        gameInfo.setGenre(gameResponse.getGenre());
        gameInfo.setPlayer(gameResponse.getPlayer());

        SimilarGamesResponse similarGamesResponse = getSimilarGames(gameResponse);
        gameInfo.setTitles(similarGamesResponse.getTitles());
        gameInfo.setDescriptions(similarGamesResponse.getDescriptions());
        gameInfo.setGenres(similarGamesResponse.getGenres());
        gameInfo.setPlayers(similarGamesResponse.getPlayers());
        gameInfo.setImages(similarGamesResponse.getImages());
        gameInfo.setLinks(similarGamesResponse.getLinks());
        return gameInfo;
    }

    public GameResponse getGame() {

    /*    Map<String, Object> body = new HashMap<>();

    body.put("model","gpt-3.5-turbo");
    body.put("prompt", GET_GAME_FIXED_PROMPT);
    body.put("temperature", 1);
    body.put("max_tokens", 50);
    body.put("top_p", 1);
    body.put("frequency_penalty", 2.0);
    body.put("presence_penalty", -2.0);

    */

        OpenApiResponse response = getApiResponse(GET_GAME_FIXED_PROMPT);

        String game = response.choices.get(0).message.getContent();

        System.out.println(game);

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

    public SimilarGamesResponse getSimilarGames(GameResponse gameResponse) {

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
                "#1 Image: <Give the URL from steam image> \n" +
                "#1 Link: <Give the URL from steam>";

        OpenApiResponse response = getApiResponse(GET_SIMILAR_GAMES_FIXED_PROMPT);

        String similarGames = response.choices.get(0).message.getContent();

        System.out.println(similarGames);

        List<String> titles = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<String> playerTypes = new ArrayList<>();
        List<String> genres = new ArrayList<>();
        List<String> images = new ArrayList<>();
        List<String> links = new ArrayList<>();

        String[] games = similarGames.split("\n\n");

        for (String game : games) {
            String[] info = game.split("\n");
            titles.add(info[0].replaceAll("(?m)^\sTitle:\s", ""));
            descriptions.add(info[1].replaceAll("(?m)^\sDescription:\s", ""));
            playerTypes.add(info[2].replaceAll("(?m)^\sPlayer type:\s", ""));
            genres.add(info[3].replaceAll("(?m)^\sGenre:\s", ""));
            images.add(info[4].replaceAll("(?m)^\sImage:\s", ""));
            links.add(info[5].replaceAll("(?m)^\sLink:\s", ""));
        }
        return new SimilarGamesResponse(titles, descriptions, genres, playerTypes, images, links);
    }

    private OpenApiResponse getApiResponse(String prompt) {
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
                .uri(URL)
                .header("Authorization", "Bearer " + API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .retrieve()
                .bodyToMono(OpenApiResponse.class)
                .block();
    }
}
