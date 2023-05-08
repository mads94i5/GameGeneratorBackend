package com.example.gamegenerator.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(columnDefinition = "TEXT(10000)")
    private String player;
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;
    @OneToMany
    private List<SimilarGame> similarGames;
    private boolean isGenerated;
    @ManyToOne
    private User user;
    @JsonManagedReference
    @OneToMany(mappedBy = "gameIdea", fetch = FetchType.LAZY)
    private List<GameCode> gameCodes;

    // Lazy fetch is used to avoid fetching the ratings when fetching the game idea
    @OneToMany(mappedBy = "gameIdea", fetch = FetchType.LAZY)
    private List<GameRating> gameRatings;


    public void addGameCode(GameCode gameCode){
        gameCodes.add(gameCode);
    }
}
