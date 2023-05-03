package com.example.gamegenerator.dto;

import com.example.gamegenerator.entity.GameIdea;
import com.example.gamegenerator.entity.SimilarGame;
import com.example.gamegenerator.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameIdeaResponse {
  private Long id;
  private String title;
  private String description;
  private String genre;
  private String player;
  private byte[] image;
  private List<SimilarGame> similarGames;
  private boolean isGenerated;
  private User user;
  public GameIdeaResponse convert(GameIdea gameIdea){
    this.id = gameIdea.getId();
    this.title = gameIdea.getTitle();
    this.description = gameIdea.getDescription();
    this.genre = gameIdea.getGenre();
    this.player = gameIdea.getPlayer();
    this.image = gameIdea.getImage();
    this.similarGames = gameIdea.getSimilarGames();
    this.isGenerated = gameIdea.isGenerated();
    this.user = gameIdea.getUser();
    return this;
  }
}
