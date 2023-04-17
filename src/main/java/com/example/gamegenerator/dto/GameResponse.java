package com.example.gamegenerator.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameResponse {

  String title;

  String description;

  String genre;

  String protagonist;

  public GameResponse(String title, String description, String genre, String protagonist) {
    this.title = title;
    this.description = description;
    this.genre = genre;
    this.protagonist = protagonist;
  }
}
