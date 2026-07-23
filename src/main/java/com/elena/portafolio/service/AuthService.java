package com.elena.portafolio.service;

import com.elena.portafolio.dto.LoginResponse;
import com.elena.portafolio.entity.User;
import com.elena.portafolio.repository.UserRepository;
import com.elena.portafolio.security.service.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;

    }


    public LoginResponse login(
            String username,
            String password) {


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );


        User user = userRepository.findByUsername(username)
                .orElseThrow();


        String token = jwtService.generateToken(
                user.getUsername()
        );


        return new LoginResponse(
                token,
                user.getUsername(),
                user.getRole()
        );
    }

}