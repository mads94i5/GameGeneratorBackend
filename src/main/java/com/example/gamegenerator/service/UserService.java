package com.example.gamegenerator.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.gamegenerator.dto.UserResponse;
import com.example.gamegenerator.entity.User;
import com.example.gamegenerator.repository.UserRepository;

/**
 * The user service is only implemented to check if a user has any credits left.
 * It is not meant for modifying the user in any way, for security reasons.
 */
@Service
public class UserService {
    
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse findByUsername(String username) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return new UserResponse(user.get());
    }
    public User checkIfUserHasXCreditsAndUse(Jwt jwt, int credits) {
        System.out.println("## Checking User credits...");
        Optional<User> optionalUser = userRepository.findById(jwt.getSubject());
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = optionalUser.get();
        if (user.getCredits() < credits) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not enough credits");
        }
        System.out.println("## Using <" + credits + "> credits of <" + user.getCredits() + "> total from user: " + user.getUsername());
        user.setCredits(user.getCredits() - credits);
        userRepository.save(user);
        return user;
    }
}
