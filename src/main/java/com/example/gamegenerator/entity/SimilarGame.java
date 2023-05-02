package com.example.gamegenerator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimilarGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT(10000)")
    private String description;
    private String genre;
    private String player;
    private String image;
    private String link;

    public SimilarGame(String title, String description, String genre, String player, String image, String link) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.player = player;
        this.image = image;
        this.link = link;
    }
}
