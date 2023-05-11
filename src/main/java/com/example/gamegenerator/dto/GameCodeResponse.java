package com.example.gamegenerator.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameCodeResponse {
    private Long id;
  
    private CodeLanguageResponse codeLanguage;

    private List<CodeClassResponse> codeClasses;
}
