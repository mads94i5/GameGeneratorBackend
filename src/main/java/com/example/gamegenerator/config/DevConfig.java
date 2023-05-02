package com.example.gamegenerator.config;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.gamegenerator.entity.User;
import com.example.gamegenerator.repository.UserRepository;
import com.example.gamegenerator.security.enums.Role;

/**
 * Setup a default user for development purposes.
 * Note: This class is only used when the "dev" profile is active,
 * which should only be the case when running the application locally.
 */
@Configuration
@Profile("dev")
public class DevConfig implements ApplicationRunner {

    private static String devUsername = "user";
    private static String devPassword = "pass";
    private static int devCredits = 10;
    private static List<Role> devRoles = List.of(Role.USER, Role.ADMIN);

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public DevConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(ApplicationArguments args) {
        devPassword = passwordEncoder.encode(devPassword);
        userRepository.save(new User(devUsername, devPassword, devCredits, devRoles));
    }
}
