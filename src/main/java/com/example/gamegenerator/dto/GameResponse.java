package com.example.gamegenerator.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameResponse {

    private String title;

    private String description;

    private String genre;

    private String player;

    public GameResponse(String title, String description, String genre, String player) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.player = player;
    }
}
