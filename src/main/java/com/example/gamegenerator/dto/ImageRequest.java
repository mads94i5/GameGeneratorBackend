package com.example.gamegenerator.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageRequest {
  private String inputs;
  private int numImages = 1;
  private int size = 512;


  public ImageRequest(String prompt) {
    this.inputs = prompt;
  }
}
