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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GameService {

  @Value("${app.api-key}")
  private String API_KEY;

  String FIXED_PROMPT = "give me a random pc game idea. Use the following form for the answer:\n" +
      "Title: \n" +
      "Description: \n" +
      "Protagonist type:\n" +
      "Genre:";

  String URL = "https://api.openai.com/v1/chat/completions";

  WebClient client = WebClient.create();

  public GameResponse getGame() {

    Map<String, Object> body = new HashMap<>();

    body.put("model", "gpt-3.5-turbo");

    List<Map<String, String>> messages = new ArrayList<>();
    Map<String, String> message = new HashMap<>();
    message.put("role", "user");
    message.put("content", FIXED_PROMPT);
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


    OpenApiResponse response = client.post()
        .uri(URL)
        .header("Authorization", "Bearer " + API_KEY)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(json))
        .retrieve()
        .bodyToMono(OpenApiResponse.class)
        .block();

    //Converts response value into String

    String game = response.choices.get(0).message.getContent();

    System.out.println(game);

    //Splits the String into four seperate Strings. Maybe it should be method by itself.

    String[] gameResponseLines = game.split("\\r?\\n"); // Split the response string into lines

    String title = "";
    String description = "";
    String protagonistType = "";
    String genre = "";

    for (String line : gameResponseLines) {
      if (line.startsWith("Title:")) {
        title = line.substring(7); // Remove the "Title: " prefix
      } else if (line.startsWith("Description:")) {
        description = line.substring(13); // Remove the "Description: " prefix
      } else if (line.startsWith("Protagonist type:")) {
        protagonistType = line.substring(18); // Remove the "Protagonist type: " prefix
      } else if (line.startsWith("Genre:")) {
        genre = line.substring(7); // Remove the "Genre: " prefix
      }
    }

    System.out.println(title);
    System.out.println(description);
    System.out.println(protagonistType);
    System.out.println(genre);


    return new GameResponse(title, description, genre, protagonistType);

  }

}
