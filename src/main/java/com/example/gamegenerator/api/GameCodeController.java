package com.example.gamegenerator.api;

import com.example.gamegenerator.dto.GameCodeRequest;
import com.example.gamegenerator.dto.GameCodeResponse;
import com.example.gamegenerator.entity.GameCode;
import com.example.gamegenerator.service.GameCodeService;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/gamecode")
public class GameCodeController {
  private final GameCodeService gameCodeService;

  public GameCodeController(GameCodeService gameCodeService) {
    this.gameCodeService = gameCodeService;
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @PostMapping("/generate")
  public GameCodeResponse getOrGenerateCode(@AuthenticationPrincipal Jwt jwt, @RequestBody GameCodeRequest gameCodeRequest){
    return gameCodeService.getOrGenerateGameCode(jwt, gameCodeRequest);
  }

  @GetMapping("/public/get/{gameIdeaId}")
  public List<GameCodeResponse> getGameCodesForGameIdea(@PathVariable Long gameIdeaId){
    return gameCodeService.getGameCodesForGameIdea(gameIdeaId);
  }
}
