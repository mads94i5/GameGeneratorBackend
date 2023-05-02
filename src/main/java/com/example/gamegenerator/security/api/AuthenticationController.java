package com.example.gamegenerator.security.api;

import java.time.Instant;

import static java.util.stream.Collectors.joining;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.oauth2.jwt.*;

import com.example.gamegenerator.security.dto.AuthenticationRequest;
import com.example.gamegenerator.security.dto.AuthenticationResponse;
import com.example.gamegenerator.security.entity.UserWithRoles;
import com.example.gamegenerator.security.service.UserDetailsServiceImp;

/**
 * Controller for authentication. 
 * 
 * Original code from:
 * https://github.com/kea-spring2023/Security-start-spring2023/blob/main/src/main/java/dat3/security/api/AuthenticationController.java
 */

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    /**
     * This is the issuer of the token. It is used to verify the token.
     */
    @Value("${app.token-issuer}")
    private String tokenIssuer;

    /**
     * This is the expiration time of the token in seconds.
     */
    @Value("${app.token-expiration}")
    private long tokenExpiration;
  
    /**
     * This is the authentication manager used to authenticate the user.
     */
    private AuthenticationManager authenticationManager;

    /**
     * This is the encoder used to encode the token.
     */
    private JwtEncoder encoder;


    public AuthenticationController(AuthenticationManager authenticationManager, JwtEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    /**
     * This method is used to login the user.
     */
    @PostMapping
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {

        try {
            // Try to authenticate the user by username and password
            UsernamePasswordAuthenticationToken uat = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            Authentication authentication = authenticationManager.authenticate(uat);

            // If the authentication is successful, create a token and return it
            UserWithRoles user = (UserWithRoles) authentication.getPrincipal();
            Instant now = Instant.now();
            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(joining(" "));
            // Create the claims
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer(tokenIssuer)  //Only this for simplicity
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(tokenExpiration))
                    .subject(user.getUsername())
                    .claim("roles", scope)
                    .build();
            // Create the header
            JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
            // Encode the token
            String token = encoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

            return new AuthenticationResponse(token);

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UserDetailsServiceImp.ERROR_MSG);
        }
    }
}
