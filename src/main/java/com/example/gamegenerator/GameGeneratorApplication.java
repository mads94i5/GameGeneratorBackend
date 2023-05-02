package com.example.gamegenerator;

import com.example.gamegenerator.entity.GameInfo;
import com.example.gamegenerator.service.GameService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class GameGeneratorApplication {

  public static void main(String[] args) {
    SpringApplication.run(GameGeneratorApplication.class, args);
  }

}
