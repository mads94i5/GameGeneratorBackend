package com.example.gamegenerator.repository;

import com.example.gamegenerator.entity.CodeLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeLanguageRepository  extends JpaRepository<CodeLanguage, Long> {
    List<CodeLanguage> findByLanguage(String language);
}
