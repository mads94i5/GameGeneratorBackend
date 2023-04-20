package com.example.gamegenerator.dto;

import com.example.gamegenerator.entity.GameInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class GameInfoResponse {


  private String title;

  private String description;
  private String genre;
  private String player;

  private byte[] image;

  private List<String> titles;


  private List<String> descriptions;

  private List<String> genres;

  private List<String> players;

  private List<String> images;

  private List<String> links;



  public GameInfoResponse convert(GameInfo gameInfo){
    this.title = gameInfo.getTitle();
    this.description = gameInfo.getTitle();
    this.genre = gameInfo.getGenre();
    this.player = gameInfo.getPlayer();
    this.image = gameInfo.getImage();
    this.titles = gameInfo.getTitles();
    this.descriptions = gameInfo.getDescriptions();
    this.genres = gameInfo.getGenres();
    this.players = gameInfo.getPlayers();
    this.images = gameInfo.getImages();
    this.links = gameInfo.getLinks();

    return this;
  }
}
