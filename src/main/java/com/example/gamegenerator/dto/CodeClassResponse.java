package com.example.gamegenerator.dto;

import com.example.gamegenerator.entity.CodeClass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CodeClassResponse {
    private Long id;
    private String name;
    private String code;

    public CodeClassResponse(CodeClass codeClass) {
        this.id = codeClass.getId();
        this.name = codeClass.getName();
        this.code = codeClass.getCode();
    }
}
