package com.example.gamegenerator.repository;

import com.example.gamegenerator.entity.GameCode;
import com.example.gamegenerator.entity.GameIdea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameCodeRepository extends JpaRepository<GameCode, Long> {
    Optional<GameCode> findGameCodeByCodeLanguage_LanguageAndGameIdea(String language, GameIdea gameIdea);
    List<GameCode> findGameCodesByGameIdea(GameIdea gameIdea);
}
