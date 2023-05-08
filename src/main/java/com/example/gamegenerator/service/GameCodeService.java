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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
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

    gameCode.setZipFile(zipGameCode(savedCodeClasses.stream().map(CodeClass::getName).collect(Collectors.toList()), codeLanguage.getFileExtension(), savedCodeClasses.stream().map(CodeClass::getCode).collect(Collectors.toList())));
    gameCode.setGameIdea(gameIdea);
    gameCode.setCodeClasses(savedCodeClasses);
    return gameCodeRepository.save(gameCode);
  }

  private byte[] zipGameCode(List<String> classNames, String fileExtension, List<String> classCodes) {
    System.out.println("## Zipping files...");
    try {
      // Create a ByteArrayOutputStream to write the code files to
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      // Create a ZipOutputStream to write the ZIP file to
      ZipOutputStream zos = new ZipOutputStream(baos);
      // Create a byte array to use as the buffer
      byte[] buffer = new byte[1024];

      for (int i = 0; i < classNames.size(); i++) {
        // Create a ZipEntry with the class name and extension
        ZipEntry ze = new ZipEntry(classNames.get(i) + fileExtension);
        // Put the zip entry in the ZipOutputStream
        zos.putNextEntry(ze);
        // Write the class code to the ByteArrayOutputStream
        baos.reset(); // Clear the byte array output stream
        baos.write(classCodes.get(i).getBytes());
        // Write the ByteArrayOutputStream to the ZipOutputStream
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        int len;
        while ((len = bais.read(buffer)) > 0) {
          zos.write(buffer, 0, len);
        }
        zos.closeEntry();
        bais.close();
      }

      zos.close();
      System.out.println("## Zip file created successfully.");
      return baos.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
