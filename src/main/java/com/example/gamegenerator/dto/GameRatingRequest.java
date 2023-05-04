package com.example.gamegenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameRatingRequest {
    
    private Long gameIdeaId;
    private double score;
}
