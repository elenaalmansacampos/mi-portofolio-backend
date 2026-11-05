package com.elena.portafolio.controller;

import com.elena.portafolio.dto.LoginRequest;
import com.elena.portafolio.dto.LoginResponse;
import com.elena.portafolio.service.AuthCookieService;
import com.elena.portafolio.service.AuthService;
import com.elena.portafolio.service.AuthSession;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;
    private final AuthCookieService authCookieService;


    public AuthController(
            AuthService authService,
            AuthCookieService authCookieService
    ) {
        this.authService = authService;
        this.authCookieService = authCookieService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthSession session = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        return withRefreshCookie(session);
    }


    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(
                    name = AuthCookieService.REFRESH_COOKIE_NAME,
                    required = false
            )
            String refreshToken
    ) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("Refresh token ausente");
        }

        return withRefreshCookie(authService.refresh(refreshToken));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(
                    name = AuthCookieService.REFRESH_COOKIE_NAME,
                    required = false
            )
            String refreshToken
    ) {

        authService.logout(refreshToken);

        return ResponseEntity.noContent()
                .header(
                        HttpHeaders.SET_COOKIE,
                        authCookieService.clear().toString()
                )
                .build();
    }


    private ResponseEntity<LoginResponse> withRefreshCookie(
            AuthSession session
    ) {

        return ResponseEntity.status(HttpStatus.OK)
                .header(
                        HttpHeaders.SET_COOKIE,
                        authCookieService.create(
                                session.refreshToken(),
                                session.refreshExpiresAt()
                        ).toString()
                )
                .body(session.response());
    }
}
