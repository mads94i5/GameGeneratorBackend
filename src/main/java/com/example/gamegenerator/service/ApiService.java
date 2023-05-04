package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.ImageRequest;
import com.example.gamegenerator.dto.OpenApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiService {

  @Value("${app.api-key}")
  private String OPENAI_API_KEY;
  private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

  private final String IMAGE_API_URL = "https://api-inference.huggingface.co/models/runwayml/stable-diffusion-v1-5";
  // private final String IMAGE_API_URL = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-v2-1";
  @Value("${app.api-key-image}")
  private String IMAGE_API_KEY;

  private final WebClient client = WebClient.create();


  public Mono<OpenApiResponse> getOpenAiApiResponse(String prompt, double temperature) {

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
        .bodyToMono(OpenApiResponse.class)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No response from OpenAI API")));
  }

  public Mono<byte[]> generateImageResponse(String prompt) {
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
        .bodyToMono(byte[].class)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No AI generated image received from API")));
  }
}
