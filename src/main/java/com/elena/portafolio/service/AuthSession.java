package com.elena.portafolio.service;

import com.elena.portafolio.dto.LoginResponse;

import java.time.LocalDateTime;

public record AuthSession(
        LoginResponse response,
        String refreshToken,
        LocalDateTime refreshExpiresAt
) {
}
