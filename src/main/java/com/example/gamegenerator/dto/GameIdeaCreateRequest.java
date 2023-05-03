package com.example.gamegenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameIdeaCreateRequest {
    private String title;
    private String description;
    private String genre;
    private String player;
    String userId;
}
