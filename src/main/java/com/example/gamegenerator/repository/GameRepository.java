package com.example.gamegenerator.repository;

import com.example.gamegenerator.entity.GameInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameInfo, Long> {

  Page<GameInfo> findGameInfosByGenreContaining(String genre, Pageable pageable);
}
