package com.example.gamegenerator.api;

import com.example.gamegenerator.dto.GameCodeRequest;
import com.example.gamegenerator.entity.GameCode;
import com.example.gamegenerator.service.GameCodeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
  public GameCode getOrGenerateCode(@AuthenticationPrincipal Jwt jwt, @RequestBody GameCodeRequest gameCodeRequest){
    return gameCodeService.getOrGenerateGameCode(jwt, gameCodeRequest);
  }

  @GetMapping("/public/get/{gameIdeaId}")
  public List<GameCode> getGameCodesForGameIdea(@PathVariable Long gameIdeaId){
    return gameCodeService.getGameCodesForGameIdea(gameIdeaId);
  }

  @GetMapping("/public/getzip/{gameIdeaId}/{codeLanguage}")
  public void getGameCodesForGameIdea(HttpServletResponse response, @PathVariable Long gameIdeaId, @PathVariable String codeLanguage){
    List<GameCode> gameCodes = gameCodeService.getGameCodesForGameIdea(gameIdeaId);
    for(GameCode gameCode : gameCodes){
      if(gameCode.getCodeLanguage().getLanguage().equals(codeLanguage)) {
        try {
          response.setContentType("application/zip");
          response.setHeader("Content-Disposition", "attachment; filename=" + gameCode.getGameIdea().getTitle() + "-" + gameCode.getCodeLanguage().getLanguage() + ".zip");
          response.getOutputStream().write(gameCode.getZipFile());
          response.flushBuffer();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
