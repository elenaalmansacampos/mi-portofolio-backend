package com.elena.portafolio.security.service;

import com.elena.portafolio.dto.LoginRequest;
import com.elena.portafolio.dto.LoginResponse;
import com.elena.portafolio.entity.User;
import com.elena.portafolio.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));


        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Contraseña incorrecta");
        }


        String token = jwtService.generateToken(user.getUsername());


        return new LoginResponse(
                user.getUsername(),
                user.getRole(),
                token
        );
    }
}