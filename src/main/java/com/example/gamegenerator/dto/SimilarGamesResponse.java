package com.example.gamegenerator.dto;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Lob;
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
    private List<String> titles;

    private List<String> descriptions;
    private List<String> genres;
    private List<String> players;
    private List<String> images;
    private List<String> links;
}
