package com.example.gamegenerator.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageRequest {
  private String model = "image-alpha-001";
  private int numImages = 1;
  private int sizeX = 512;
  private int sizeY = 512;
  private double scale = 1.0;
  private double responseTimeoutInSeconds = 300.0;
  private String prompt;

  public ImageRequest(String prompt) {
    this.prompt = prompt;
  }
}
