package com.elena.portafolio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AuthCookieService {

    public static final String REFRESH_COOKIE_NAME = "refresh_token";

    private final boolean secure;
    private final String sameSite;


    public AuthCookieService(
            @Value("${auth.cookie-secure:false}") boolean secure,
            @Value("${auth.cookie-same-site:Lax}") String sameSite
    ) {
        this.secure = secure;
        this.sameSite = sameSite;
    }


    public ResponseCookie create(
            String token,
            LocalDateTime expiresAt
    ) {

        long seconds = Math.max(
                0,
                Duration.between(LocalDateTime.now(), expiresAt).getSeconds()
        );

        return base(token)
                .maxAge(Duration.ofSeconds(seconds))
                .build();
    }


    public ResponseCookie clear() {
        return base("")
                .maxAge(0)
                .build();
    }


    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api/auth");
    }
}
