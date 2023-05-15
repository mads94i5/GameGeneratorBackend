package com.example.gamegenerator.dto;

import lombok.*;

@Builder
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
