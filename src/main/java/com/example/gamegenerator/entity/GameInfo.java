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
public class GameInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT(10000)")
    private String description;
    private String genre;
    private String player;
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;
    @ElementCollection
    private List<String> titles;
    @Column(columnDefinition = "TEXT(10000)")
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
