package com.example.gamegenerator.api;

import com.example.gamegenerator.dto.GameCodeRequest;
import com.example.gamegenerator.entity.GameCode;
import com.example.gamegenerator.service.GameCodeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gamecode")
public class GameCodeController {
  private final GameCodeService gameCodeService;

  public GameCodeController(GameCodeService gameCodeService) {
    this.gameCodeService = gameCodeService;
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @GetMapping("/generate")
  public GameCode generateCode(@AuthenticationPrincipal Jwt jwt, @RequestBody GameCodeRequest gameCodeRequest){
    return gameCodeService.getOrGenerateGameCode(jwt, gameCodeRequest);
  }
}
