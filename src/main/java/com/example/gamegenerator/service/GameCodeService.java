package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.GameCodeRequest;
import com.example.gamegenerator.dto.OpenApiResponse;
import com.example.gamegenerator.entity.*;
import com.example.gamegenerator.repository.CodeClassRepository;
import com.example.gamegenerator.repository.CodeLanguageRepository;
import com.example.gamegenerator.repository.GameCodeRepository;
import com.example.gamegenerator.repository.GameIdeaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class GameCodeService {
  private final CodeClassRepository codeClassRepository;
  private final CodeLanguageRepository codeLanguageRepository;
  private final GameCodeRepository gameCodeRepository;
  private final GameIdeaRepository gameIdeaRepository;
  private final ApiService apiService;
  private final UserService userService;

  public GameCodeService(CodeClassRepository codeClassRepository, CodeLanguageRepository codeLanguageRepository, GameCodeRepository gameCodeRepository, ApiService apiService, GameIdeaRepository gameIdeaRepository, UserService userService) {
    this.codeClassRepository = codeClassRepository;
    this.codeLanguageRepository = codeLanguageRepository;
    this.gameCodeRepository = gameCodeRepository;
    this.apiService = apiService;
    this.gameIdeaRepository = gameIdeaRepository;
    this.userService = userService;
  }

  private static String getGetClassesFixedPrompt(CodeLanguage codeLanguage, GameIdea gameIdea) {
    String GET_CLASSES_FIXED_PROMPT = "I want to code a video game in " + codeLanguage.getLanguage() + "\n" +
            "Please give me a complete list of " + codeLanguage.getLanguage() + " class names that I would need to complete the game from the following information.\n" +
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
    return GET_CLASSES_FIXED_PROMPT;
  }

  private static String getCodeFixedPrompt(CodeLanguage codeLanguage, GameIdea gameIdea, String[] gameCodeClasses, String className, CodeClass codeClass) {
    String GET_CODE_FIXED_PROMPT = "I want to code a video game in " + codeLanguage.getLanguage() + "\n" +
            "Please give me the code of the class: " + codeClass.getName() + " from the following information.\n" +
            "You can make assumptions from the following information and you must come up with complete features that would be needed to make a functional game.\n" +
            "Please do not include explanations or preliminary text presenting the code class." +
            "Title: " + gameIdea.getTitle() + "\n" +
            "Description: " + gameIdea.getDescription() + "\n" +
            "Genre: " + gameIdea.getGenre() + "\n" +
            "You play as a: " + gameIdea.getPlayer() + "\n" +
            "Make the class: " + className + "\n" +
            "From this full list of classes: \n";
    for (String gameCodeClass : gameCodeClasses) {
      GET_CODE_FIXED_PROMPT += gameCodeClass + "\n";
    }
    return GET_CODE_FIXED_PROMPT;
  }

  public GameCode getOrGenerateGameCode(Jwt jwt, GameCodeRequest gameCodeRequest){
    GameIdea gameIdea = gameIdeaRepository.findById(gameCodeRequest.getGameIdeaId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such game"));

    Optional<GameCode> databaseGameCode = gameCodeRepository.findGameCodeByCodeLanguage_LanguageAndGameIdea(gameCodeRequest.getLanguage(), gameIdea);
    if (databaseGameCode.isPresent()) {
      System.out.println("## Found cached game code in database for " + gameIdea.getTitle() + " in " + gameCodeRequest.getLanguage());
      return databaseGameCode.get();
    }
    System.out.println("## No cache: Generating game code in database for " + gameIdea.getTitle() + " in " + gameCodeRequest.getLanguage());

    GameCode gameCode = new GameCode();

    userService.checkIfUserHasXCreditsAndUse(jwt, 1);

    CodeLanguage codeLanguage = new CodeLanguage(gameCodeRequest.getLanguage());

    if (codeLanguageRepository.findByLanguage(gameCodeRequest.getLanguage()).isEmpty()) {
      codeLanguageRepository.save(codeLanguage);
    } else {
      codeLanguage = codeLanguageRepository.findByLanguage(gameCodeRequest.getLanguage()).get(0);
    }

    gameCode.setCodeLanguage(codeLanguage);

    List<CodeClass> codeClasses = new ArrayList<>();

    String GET_CLASSES_FIXED_PROMPT = getGetClassesFixedPrompt(codeLanguage, gameIdea);

    System.out.println("## Requesting classes for : " + gameIdea.getTitle());
    OpenApiResponse getClassesResponse = apiService.getOpenAiApiResponse(GET_CLASSES_FIXED_PROMPT, 0).block();

    String classList = getClassesResponse.choices.get(0).message.getContent();

    System.out.println(classList);

    List<Mono<OpenApiResponse>> monos = new ArrayList<>();

    String[] gameCodeClassNames = classList.substring(classList.indexOf("\n\n") + 1).split("\\r?\\n");

    for (String className : gameCodeClassNames) {
      if (className.contains(" ")) {
        className = className.substring(0, className.indexOf(" "));
      }
      CodeClass codeClass = new CodeClass();
      codeClass.setName(className);
      codeClasses.add(codeClass);
      String GET_CODE_FIXED_PROMPT = getCodeFixedPrompt(codeLanguage, gameIdea, gameCodeClassNames, className, codeClass);

      try {
        Thread.sleep(10000); // Introduce a delay between API requests
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("## Requesting code for class: " + className);
      monos.add(apiService.getOpenAiApiResponse(GET_CODE_FIXED_PROMPT, 0).subscribeOn(Schedulers.boundedElastic()));
    }

    Mono.zip(monos, responses -> IntStream.range(0, responses.length)
            .mapToObj(i -> {
              OpenApiResponse openApiResponse = (OpenApiResponse) responses[i];
              String code = openApiResponse.choices.get(0).message.getContent();
              codeClasses.get(i).setCode(code);
              System.out.println(code);
              return codeClasses.get(i);
            })
            .collect(Collectors.toList())).block();
    List<CodeClass> savedCodeClasses = new ArrayList<>();
    for (CodeClass codeClazz : codeClasses) {
        savedCodeClasses.add(codeClassRepository.save(codeClazz));
    }

    gameCode.setCodeClasses(savedCodeClasses);
    GameCode savedGameCode = gameCodeRepository.save(gameCode);

    gameIdea.addGameCode(savedGameCode);
    gameIdeaRepository.save(gameIdea);

    return gameCode;
  }
}
