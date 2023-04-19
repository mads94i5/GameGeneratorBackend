package com.example.gamegenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
  private String prompt;
  private int numImages;
  private int size;
  private String model;


  public ImageRequest(String prompt) {
    this.prompt = prompt;
  }
}
