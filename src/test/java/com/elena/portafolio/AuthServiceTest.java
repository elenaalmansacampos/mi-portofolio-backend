package com.elena.portafolio;

import com.elena.portafolio.entity.User;
import com.elena.portafolio.repository.RefreshTokenRepository;
import com.elena.portafolio.repository.UserRepository;
import com.elena.portafolio.service.AuthService;
import com.elena.portafolio.service.AuthSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    private static final String PASSWORD = "test-password";

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.save(new User(
                "Elena",
                passwordEncoder.encode(PASSWORD),
                "ADMIN"
        ));
    }

    @Test
    void loginDevuelveAccessTokenYRefreshToken() {
        AuthSession session = authService.login("Elena", PASSWORD);

        assertNotNull(session.response().getToken());
        assertEquals("Elena", session.response().getUsername());
        assertEquals("ADMIN", session.response().getRole());
        assertNotNull(session.refreshToken());
        assertTrue(session.refreshExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void refreshRotaElTokenYRechazaElAnterior() {
        AuthSession first = authService.login("Elena", PASSWORD);
        AuthSession second = authService.refresh(first.refreshToken());

        assertNotNull(second.response().getToken());
        assertNotEquals(first.refreshToken(), second.refreshToken());

        assertThrows(BadCredentialsException.class, () ->
                authService.refresh(first.refreshToken())
        );
    }

    @Test
    void logoutRevocaElRefreshToken() {
        AuthSession session = authService.login("Elena", PASSWORD);

        authService.logout(session.refreshToken());

        assertThrows(BadCredentialsException.class, () ->
                authService.refresh(session.refreshToken())
        );
    }

    @Test
    void loginConContrasenaIncorrectaLanzaExcepcion() {
        assertThrows(BadCredentialsException.class, () ->
                authService.login("Elena", "contraseña-incorrecta")
        );
    }

    @Test
    void loginConUsuarioInexistenteLanzaExcepcion() {
        assertThrows(BadCredentialsException.class, () ->
                authService.login("usuario-inventado", PASSWORD)
        );
    }
}
