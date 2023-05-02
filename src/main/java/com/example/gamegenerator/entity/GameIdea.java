package com.example.gamegenerator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameIdea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT(10000)")
    private String description;
    private String genre;
    private String player;
    @OneToMany
    private List<GameMechanic> gameMechanics;
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;
    @OneToMany
    private List<SimilarGame> similarGames;
    private boolean isGenerated;
}
