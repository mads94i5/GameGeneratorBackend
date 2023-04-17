package com.example.gamegenerator.api;


import com.example.gamegenerator.dto.GameResponse;
import com.example.gamegenerator.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/game-idea")
public class GameController {

GameService service;

  public GameController(GameService service) {
    this.service = service;
  }

@GetMapping
  public GameResponse getGame(){
    return service.getGame();
}

}
