package com.elena.portafolio.service;

import com.elena.portafolio.entity.RefreshToken;
import com.elena.portafolio.repository.RefreshTokenRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long expirationDays;
    private final SecureRandom secureRandom = new SecureRandom();


    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${refresh-token.expiration-days:14}")
            long expirationDays
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.expirationDays = expirationDays;
    }


    @Transactional
    public IssuedRefreshToken create(Long userId) {

        String rawToken = generateToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(hash(rawToken));
        refreshToken.setUserId(userId);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(expirationDays));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return new IssuedRefreshToken(
                rawToken,
                refreshToken.getExpiresAt()
        );
    }


    @Transactional
    public RotatedRefreshToken rotate(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new BadCredentialsException("Refresh token inválido");
        }

        RefreshToken stored = refreshTokenRepository
                .findByTokenHash(hash(rawToken))
                .orElseThrow(() ->
                        new BadCredentialsException("Refresh token inválido"));

        if (stored.isRevoked()) {
            throw new BadCredentialsException("Refresh token revocado");
        }

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw new BadCredentialsException("Refresh token caducado");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        IssuedRefreshToken issued = create(stored.getUserId());

        return new RotatedRefreshToken(
                stored.getUserId(),
                issued.token(),
                issued.expiresAt()
        );
    }


    @Transactional
    public void revoke(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        refreshTokenRepository.findByTokenHash(hash(rawToken))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }


    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(
                LocalDateTime.now().minusDays(1)
        );
    }


    private String generateToken() {

        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }


    private String hash(String rawToken) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no disponible", exception);
        }
    }


    public record IssuedRefreshToken(
            String token,
            LocalDateTime expiresAt
    ) {
    }


    public record RotatedRefreshToken(
            Long userId,
            String token,
            LocalDateTime expiresAt
    ) {
    }
}
