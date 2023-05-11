package com.example.gamegenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameRatingResponse {
    private Long gameIdeaId;
    private double totalScoreInPercent;
    private int numberOfRatings;
}
