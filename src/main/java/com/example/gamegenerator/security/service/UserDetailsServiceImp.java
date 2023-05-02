package com.example.gamegenerator.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.gamegenerator.security.repository.UserWithRolesRepository;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    public static String ERROR_MSG = "The credentials was wrong, please try again";

    private UserWithRolesRepository userWithRolesRepository;

    public UserDetailsServiceImp(UserWithRolesRepository userWithRolesRepository) {
        this.userWithRolesRepository = userWithRolesRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userWithRolesRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException(ERROR_MSG));
    }
}
