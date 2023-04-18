package com.example.gamegenerator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    private String description;
    private String genre;
    private String player;
    @ElementCollection
    private List<String> titles;
    @ElementCollection
    private List<String> descriptions;
    @ElementCollection
    private List<String> genres;
    @ElementCollection
    private List<String> players;
    @ElementCollection
    private List<String> images;
    @ElementCollection
    private List<String> links;
}
