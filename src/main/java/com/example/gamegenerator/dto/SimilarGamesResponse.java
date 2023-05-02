package com.example.gamegenerator.dto;

import com.example.gamegenerator.entity.SimilarGame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimilarGamesResponse {
    private List<SimilarGame> similarGames;
}
