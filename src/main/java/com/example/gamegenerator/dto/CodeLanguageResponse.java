package com.example.gamegenerator.dto;

import com.example.gamegenerator.entity.CodeLanguage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CodeLanguageResponse {
    private Long id;
    private String language;
    private String fileExtension;

    public CodeLanguageResponse(CodeLanguage codeLanguage) {
        this.id = codeLanguage.getId();
        this.language = codeLanguage.getLanguage();
        this.fileExtension = codeLanguage.getFileExtension();
    }
}
