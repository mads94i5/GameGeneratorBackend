package com.example.gamegenerator.service;

import com.example.gamegenerator.dto.CodeClassResponse;
import com.example.gamegenerator.dto.CodeLanguageResponse;
import com.example.gamegenerator.dto.GameCodeRequest;
import com.example.gamegenerator.dto.GameCodeResponse;
import com.example.gamegenerator.dto.OpenApiResponse;
import com.example.gamegenerator.entity.*;
import com.example.gamegenerator.repository.CodeClassRepository;
import com.example.gamegenerator.repository.CodeLanguageRepository;
import com.example.gamegenerator.repository.GameCodeRepository;
import com.example.gamegenerator.repository.GameIdeaRepository;
import com.example.gamegenerator.utils.ZipUtils;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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
    return "I want to code a video game in " + codeLanguage.getLanguage() + "\n" +
            "Please give me a complete list of " + codeLanguage.getLanguage() + " class names that I would need to complete the game from the following information.\n" +
            "You can make assumptions from the following information to come up with a complete list of classes that would be needed to finish the game, do not arbitrarily limit the number of classes.\n" +
            "There will likely be a lot of class names needed, but only give me the necessary ones. Don't arbitrarily come up with ones that do not make sense for the functionality of the game. \n" +
            "Also be completely sure follow the following restrictions: Only include the actual names of classes in your response.\n" +
            "So please do not include explanations or preliminary text presenting the class names, like \"Here are the names of classes...\"." +
            "Also don't include any parenthesis explaining anything, only include the names of the classes seperated by new lines in your response." +
            "So don't present them as a html list:" +
            "Title: " + gameIdea.getTitle() +
            "Description: " + gameIdea.getDescription() +
            "Genre: " + gameIdea.getGenre() +
            "You play as a: " + gameIdea.getPlayer();
  }

  private static String getCodeFixedPrompt(CodeLanguage codeLanguage, GameIdea gameIdea, List<String> gameCodeClasses, String className, CodeClass codeClass) {
    String GET_CODE_FIXED_PROMPT = "I want to code a video game in " + codeLanguage.getLanguage() + "\n" +
            "Please give me functional code of the class: " + codeClass.getName() + " from the following information.\n" +
            "You can make assumptions from the following information and you must come up with features that would be needed to make a functional game.\n" +
            "Please do not include explanations or preliminary text presenting the code class. \n" +
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

  public GameCodeResponse getOrGenerateGameCode(Jwt jwt, GameCodeRequest gameCodeRequest){
    GameIdea gameIdea = gameIdeaRepository.findById(gameCodeRequest.getGameIdeaId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such game"));
    if (gameCodeRequest.getLanguage() == null || gameCodeRequest.getLanguage().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No language specified");
    }
    Optional<GameCode> databaseGameCode = gameCodeRepository.findGameCodeByCodeLanguage_LanguageAndGameIdea(gameCodeRequest.getLanguage(), gameIdea);
    if (databaseGameCode.isPresent()) {
      System.out.println("## Found cached game code in database for " + gameIdea.getTitle() + " in " + gameCodeRequest.getLanguage());
      GameCode gameCode = databaseGameCode.get();
      return createResponse(gameCode);
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

    List<String> gameCodeClassNames = Arrays.stream(classList.substring(classList.indexOf("\n\n") + 1).split("\\r?\\n")).toList();

    CodeLanguage finalCodeLanguage = codeLanguage;
    Flux.fromIterable(gameCodeClassNames)
            .flatMap(className -> {
              if (className.contains(" ")) {
                className = className.substring(0, className.indexOf(" "));
              }
              CodeClass codeClass = new CodeClass();
              codeClass.setName(className);
              codeClasses.add(codeClass);
              String GET_CODE_FIXED_PROMPT = getCodeFixedPrompt(finalCodeLanguage, gameIdea, gameCodeClassNames, className, codeClass);

              return apiService.getOpenAiApiResponse(GET_CODE_FIXED_PROMPT, 0)
                      .flux()
                      .delayElements(Duration.ofSeconds(15))
                      .retryWhen(Retry.backoff(3, Duration.ofSeconds(10)))
                      .subscribeOn(Schedulers.boundedElastic())
                      .map(openApiResponse -> {
                        String code = openApiResponse.choices.get(0).message.getContent();
                        codeClass.setCode(code);
                        System.out.println(code);
                        return codeClass;
                      });
            })
            .collectList()
            .block();

    List<CodeClass> savedCodeClasses = codeClassRepository.saveAll(codeClasses);
    
    gameCode.setGameIdea(gameIdea);
    gameCode.setCodeClasses(savedCodeClasses);
    gameCode = gameCodeRepository.save(gameCode);
    return createResponse(gameCode);
  }

  public List<GameCodeResponse> getGameCodesForGameIdea(Long gameIdeaId) {
    GameIdea gameIdea = gameIdeaRepository.findById(gameIdeaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such game"));
    List<GameCode> optionalGameCodes = gameCodeRepository.findGameCodesByGameIdea(gameIdea);
    return optionalGameCodes.stream().map(gameCode -> createResponse(gameCode)).collect(Collectors.toList());
  }

  /**
   * A method that returns a downloadable zip file of the game code
   * @param gameCodeId
   * @return Zip file
   */
  public File getZipFileForGameCode(Long gameCodeId) {
    GameCode gameCode = gameCodeRepository.findById(gameCodeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such game code"));
    // Get language and code information
    CodeLanguage codeLanguage = gameCode.getCodeLanguage();
    List<CodeClass> codeClasses = gameCode.getCodeClasses();
    List<String> codeClassNames = codeClasses.stream().map(codeClass -> codeClass.getName()).collect(Collectors.toList());
    List<String> codeClassCodes = codeClasses.stream().map(codeClass -> codeClass.getCode()).collect(Collectors.toList());
    
    // create temp files
    List<File> files = ZipUtils.writeCodeToTempFiles(codeLanguage.getFileExtension(), codeClassNames, codeClassCodes);
    
    // Create zip name
    String zipName = gameCode.getGameIdea().getTitle() + "_" + codeLanguage.getLanguage() + ".zip";

    // Create zip file
    File zipFile = ZipUtils.writeFilesToZip(zipName, files);

    // Return zip file
    return zipFile;
  }

  /**
   * A method to create a response from a game code
   * @param gameCode
   * @return GameCodeResponse
   */
  private GameCodeResponse createResponse(GameCode gameCode) {
    CodeLanguageResponse codeLanguageResponse = new CodeLanguageResponse(gameCode.getCodeLanguage());
    List<CodeClassResponse> codeClassResponses = gameCode.getCodeClasses().stream().map(codeClass -> new CodeClassResponse(codeClass)).collect(Collectors.toList());
    return new GameCodeResponse(gameCode.getId(), codeLanguageResponse, codeClassResponses);
  }
}
