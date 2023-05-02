package com.example.gamegenerator.dto;

import com.example.gamegenerator.entity.GameMechanic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameIdeaCreateRequest {
    private String title;
    private String description;
    private String genre;
    private String player;
    private List<GameMechanic> gameMechanics;
    String userId;
}
