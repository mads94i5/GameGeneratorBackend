package com.example.gamegenerator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
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
    FILE_EXTENSIONS.put(Pattern.compile("javascript(?!\\S)", Pattern.CASE_INSENSITIVE), ".js");
    FILE_EXTENSIONS.put(Pattern.compile("java(?!\\S)", Pattern.CASE_INSENSITIVE), ".java");
    FILE_EXTENSIONS.put(Pattern.compile("python(?!\\S)", Pattern.CASE_INSENSITIVE), ".py");
    FILE_EXTENSIONS.put(Pattern.compile("c\\+\\+(?!\\S)", Pattern.CASE_INSENSITIVE), ".cpp");
    FILE_EXTENSIONS.put(Pattern.compile("c#(?!\\S)", Pattern.CASE_INSENSITIVE), ".cs");
    FILE_EXTENSIONS.put(Pattern.compile("c(?!\\S)", Pattern.CASE_INSENSITIVE), ".c");
    FILE_EXTENSIONS.put(Pattern.compile("objective-c(?!\\S)", Pattern.CASE_INSENSITIVE), ".m");
    FILE_EXTENSIONS.put(Pattern.compile("swift(?!\\S)", Pattern.CASE_INSENSITIVE), ".swift");
    FILE_EXTENSIONS.put(Pattern.compile("lua(?!\\S)", Pattern.CASE_INSENSITIVE), ".lua");
    FILE_EXTENSIONS.put(Pattern.compile("typescript(?!\\S)", Pattern.CASE_INSENSITIVE), ".ts");
    FILE_EXTENSIONS.put(Pattern.compile("rust(?!\\S)", Pattern.CASE_INSENSITIVE), ".rs");
    FILE_EXTENSIONS.put(Pattern.compile("kotlin(?!\\S)", Pattern.CASE_INSENSITIVE), ".kt");
    FILE_EXTENSIONS.put(Pattern.compile("assembly(?!\\S)", Pattern.CASE_INSENSITIVE), ".asm");
  }

  public CodeLanguage(String language) {
    this.language = language;
    if (this.language == null) {
      this.fileExtension = ".txt";
      return;
    }
    for (Pattern pattern : FILE_EXTENSIONS.keySet()) {
      if (pattern.matcher(this.language).find()) {
        this.fileExtension = FILE_EXTENSIONS.get(pattern);
        return;
      }
    }
    this.fileExtension = ".txt";
  }
}
