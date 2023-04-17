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

import java.util.HashMap;
import java.util.Map;

@Service
public class GameService {

  @Value("${app.api-key}")
  private String API_KEY;

  String FIXED_PROMPT = "give me a random pc game idea. Use the following form for the answer:\n" +
      "Description: \n" +
      "Protaganist type:\n" +
      "Genre:";

  String URL = "";

  WebClient client = WebClient.create();

  public GameResponse getGame() {

/*    Map<String, Object> body = new HashMap<>();

    body.put("model","text-davinci-003");
    body.put("prompt", FIXED_PROMPT);
    body.put("temperature", 1);
    body.put("max_tokens", 50);
    body.put("top_p", 1);
    body.put("frequency_penalty", 0.2);
    body.put("presence_penalty", 0);

    ObjectMapper mapper = new ObjectMapper();
    String json = "";
    try {
      json = mapper.writeValueAsString(body);
    } catch (Exception e) {
      e.printStackTrace();
    }*/

    String json = "";

    OpenApiResponse response = client.post()
        .uri(URL)
        .header("Authorization", "Bearer" + API_KEY)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(json))
        .retrieve()
        .bodyToMono(OpenApiResponse.class)
        .block();

    //Needs to convert response value into String

    return new GameResponse(response);

  }
}
