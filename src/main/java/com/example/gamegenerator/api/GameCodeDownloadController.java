package com.example.gamegenerator.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.gamegenerator.service.GameCodeService;

@Controller
public class GameCodeDownloadController {

    private final GameCodeService gameCodeService;

  public GameCodeDownloadController(GameCodeService gameCodeService) {
    this.gameCodeService = gameCodeService;
  }

  @GetMapping("/gamecode/download/{gameCodeId}")
  public ResponseEntity<Resource> downloadCode(@PathVariable Long gameCodeId)
    throws IOException {
    File file = gameCodeService.getZipFileForGameCode(gameCodeId);
    ByteArrayResource resource = new ByteArrayResource(
      Files.readAllBytes(file.toPath())
    );

    return ResponseEntity
      .ok()
      .header(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + file.toPath().getFileName().toString()
      )
      .contentType(MediaType.APPLICATION_OCTET_STREAM)
      .contentLength(resource.contentLength())
      .body(resource);
  }
}
