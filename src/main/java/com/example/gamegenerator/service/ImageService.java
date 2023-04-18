package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.ImageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ImageService {
  private static final String API_URL = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-2-1";

  @Value("${app.api-key-image}")
  private String API_KEY;

  private WebClient webClient = WebClient.create();



  public byte[] generateImage(String prompt) {
    // Set up request data
    ImageRequest request = new ImageRequest();
    request.setPrompt(prompt);


    // Make request to API
    byte[] responseBytes = webClient.post()
        .uri(API_URL)
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + API_KEY)
        .body(Mono.just(request), ImageRequest.class)
        .retrieve()
        .bodyToMono(byte[].class)
        .block();

    return responseBytes;
  }
}

