package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.GameResponse;
import com.example.gamegenerator.dto.OpenApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameService {

  @Value("${app.api-key}")
  private String API_KEY;

  String FIXED_PROMPT = "give me a random pc game idea. Use the following form for the answer:\n" +
      "Description: \n" +
      "Protaganist type:\n" +
      "Genre:";

  String URL = "https://api.openai.com/v1/chat/completions";

  WebClient client = WebClient.create();

  public GameResponse getGame() {

/*    Map<String, Object> body = new HashMap<>();

    body.put("model","gpt-3.5-turbo");
    body.put("prompt", FIXED_PROMPT);
    body.put("temperature", 1);
    body.put("max_tokens", 50);
    body.put("top_p", 1);
    body.put("frequency_penalty", 2.0);
    body.put("presence_penalty", -2.0);*/

    Map<String, Object> body = new HashMap<>();

    body.put("model", "gpt-3.5-turbo");

    List<Map<String, String>> messages = new ArrayList<>();
    Map<String, String> message = new HashMap<>();
    message.put("role", "user");
    message.put("content", FIXED_PROMPT);
    messages.add(message);

    body.put("messages", messages);

    body.put("temperature", 2);


    ObjectMapper mapper = new ObjectMapper();
    String json = "";
    try {
      json = mapper.writeValueAsString(body);
    } catch (Exception e) {
      e.printStackTrace();
    }


    OpenApiResponse response = client.post()
        .uri(URL)
        .header("Authorization", "Bearer " + API_KEY)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(json))
        .retrieve()
        .bodyToMono(OpenApiResponse.class)
        .block();

    //Needs to convert response value into String

    String game = response.choices.get(0).text;

    int startDesc = game.indexOf("Description: ") + 13;
    int endDesc = game.indexOf("\n \nProtaganist type:");
    String description = game.substring(startDesc, endDesc);

// Extract the protagonist type string
    int startProtag = game.indexOf("Protaganist type: ") + 18;
    int endProtag = game.indexOf("\n\nGenre:");
    String protagonistType = game.substring(startProtag, endProtag);

// Extract the genre string
    int startGenre = game.indexOf("Genre: ") + 7;
    String genre = game.substring(startGenre);


    return new GameResponse(description, genre, protagonistType);

  }

}
