package com.example.gamegenerator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CodeLanguage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String language;
  private String fileExtension;
  public CodeLanguage(String language) {
    this.language = language;
    switch (language){
      case "java" -> this.fileExtension = ".java";
      case "python" -> this.fileExtension = ".py";
      case "javascript" -> this.fileExtension = ".js";
      case "c++" -> this.fileExtension = ".cpp";
      case "c#" -> this.fileExtension = ".cs";
      default -> this.fileExtension = ".txt";
    }
  }
}
