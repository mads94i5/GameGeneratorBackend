package com.example.gamegenerator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

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
  private static final HashMap<Pattern, String> FILE_EXTENSIONS = new HashMap<>();

  static {
    FILE_EXTENSIONS.put(Pattern.compile("java", Pattern.CASE_INSENSITIVE), ".java");
    FILE_EXTENSIONS.put(Pattern.compile("python", Pattern.CASE_INSENSITIVE), ".py");
    FILE_EXTENSIONS.put(Pattern.compile("javascript", Pattern.CASE_INSENSITIVE), ".js");
    FILE_EXTENSIONS.put(Pattern.compile("c\\+\\+", Pattern.CASE_INSENSITIVE), ".cpp");
    FILE_EXTENSIONS.put(Pattern.compile("c#", Pattern.CASE_INSENSITIVE), ".cs");
  }

  public CodeLanguage(String language) {
    this.language = language;
    for (Pattern pattern : FILE_EXTENSIONS.keySet()) {
      if (pattern.matcher(this.language).find()) {
        this.fileExtension = FILE_EXTENSIONS.get(pattern);
        return;
      }
    }
    this.fileExtension = ".txt";
  }
}
