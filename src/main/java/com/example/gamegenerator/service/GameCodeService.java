package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.GameCodeRequest;
import com.example.gamegenerator.dto.OpenApiResponse;
import com.example.gamegenerator.entity.CodeClass;
import com.example.gamegenerator.entity.CodeLanguage;
import com.example.gamegenerator.entity.GameCode;
import com.example.gamegenerator.entity.GameIdea;
import com.example.gamegenerator.repository.CodeClassRepository;
import com.example.gamegenerator.repository.CodeLanguageRepository;
import com.example.gamegenerator.repository.GameCodeRepository;
import com.example.gamegenerator.repository.GameIdeaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameCodeService {

  private final CodeClassRepository codeClassRepository;

  private final CodeLanguageRepository codeLanguageRepository;

  private final GameCodeRepository gameCodeRepository;

  private final GameIdeaRepository gameIdeaRepository;


  private final ApiService apiService;

  public GameCodeService(CodeClassRepository codeClassRepository, CodeLanguageRepository codeLanguageRepository, GameCodeRepository gameCodeRepository, ApiService apiService, GameIdeaRepository gameIdeaRepository) {
    this.codeClassRepository = codeClassRepository;
    this.codeLanguageRepository = codeLanguageRepository;
    this.gameCodeRepository = gameCodeRepository;
    this.apiService = apiService;
    this.gameIdeaRepository = gameIdeaRepository;
  }

  public GameCode codeGenerator(Jwt jwt, GameCodeRequest gameCodeRequest){

    GameCode gameCode = new GameCode();

    CodeLanguage codeLanguage = new CodeLanguage(gameCodeRequest.getLanguage());

    codeLanguageRepository.save(codeLanguage);

    gameCode.setCodeLanguage(codeLanguage);

    List<CodeClass> codeClasses = new ArrayList<>();

    GameIdea gameIdea = gameIdeaRepository.findById(gameCodeRequest.getGameIdeaId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such game"));

    String GET_CODE_FIXED_PROMPT = "I want to code a video game in " + codeLanguage.getLanguage() + "\n" +
        "Please give me a complete list of Java class names that I would need to complete the game from the following information.\n" +
        "You can make assumptions from the following information to come up with a complete list of classes that would be needed to finish the game, do not arbitrarily limit the number of classes.\n" +
        "There will likely be a lot of class names needed." +
        "Also be completely sure follow the following restrictions: Only include the actual names of classes in your response.\n" +
        "So please do not include explanations or preliminary text presenting the class names, like \"Here are the names of classes...\"." +
        "Also don't include any parenthesis explaining anything, only include the names of the classes seperated by new lines in your response." +
        "So don't present them as a html list:" +
        "Title: " + gameIdea.getTitle() +
        "Description: " + gameIdea.getDescription() +
        "Genre: " + gameIdea.getGenre() +
        "You play as a: " + gameIdea.getPlayer();

    OpenApiResponse response = apiService.getOpenAiApiResponse(GET_CODE_FIXED_PROMPT, 0).block();

    String classList = response.choices.get(0).message.getContent();

    System.out.println(classList);

    int startIndex = classList.indexOf("\n\n") + 1;

    String result = classList.substring(startIndex);

    String[] gameCodeClasses = result.split("\\r?\\n");

    for (String line : gameCodeClasses) {
    CodeClass codeClass = new CodeClass();
    codeClass.setName(line);
    codeClasses.add(codeClass);
      }

    codeClassRepository.saveAll(codeClasses);
    gameCode.setCodeClasses(codeClasses);


    gameCodeRepository.save(gameCode);
    gameIdea.addGameCode(gameCode);
    gameIdeaRepository.save(gameIdea);

    return gameCode;
  }

}
