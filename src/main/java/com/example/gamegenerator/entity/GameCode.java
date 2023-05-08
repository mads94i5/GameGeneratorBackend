package com.example.gamegenerator.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.apache.bcel.classfile.Code;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GameCode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  private CodeLanguage codeLanguage;
  @OneToMany
  private List<CodeClass> codeClasses;
  @JsonBackReference
  @ManyToOne
  private GameIdea gameIdea;
  @Column(columnDefinition = "LONGBLOB")
  private byte[] zipFile;
}
