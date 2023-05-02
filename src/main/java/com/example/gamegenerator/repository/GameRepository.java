package com.example.gamegenerator.repository;

import com.example.gamegenerator.entity.GameIdea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameIdea, Long> {

  Page<GameIdea> findGameInfosByGenreContaining(String genre, Pageable pageable);
}
