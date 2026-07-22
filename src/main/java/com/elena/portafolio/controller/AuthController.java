package com.elena.portafolio.controller;

import com.elena.portafolio.dto.LoginRequest;
import com.elena.portafolio.dto.LoginResponse;
import com.elena.portafolio.security.service.AuthService;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        return authService.login(request);
    }
}