package com.elena.portafolio.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMinutes;


    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes:15}") long expirationMinutes
    ) {

        if (secret == null
                || secret.getBytes(StandardCharsets.UTF_8).length < 32) {

            throw new IllegalArgumentException(
                    "jwt.secret debe tener al menos 32 bytes"
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
        this.expirationMinutes = expirationMinutes;
    }


    public String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                + expirationMinutes * 60_000
                        )
                )
                .signWith(signingKey)
                .compact();
    }


    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername());
    }

}
