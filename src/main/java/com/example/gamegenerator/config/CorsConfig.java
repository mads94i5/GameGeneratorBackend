package com.example.gamegenerator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

  @Configuration
  @EnableWebMvc
  public class CorsConfig implements WebMvcConfigurer {

    private static final String[] ALLOWED_ORIGINS = new String[] {
        //Add deployed frontend url to this list
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "https://gamegenerator.swalkstudios.com",
        "black-island-036ceea03.3.azurestaticapps.net"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
          .allowedOrigins(ALLOWED_ORIGINS)
          .allowedMethods("GET", "POST", "HEAD", "OPTIONS")
          .allowCredentials(true);
    }
  }

