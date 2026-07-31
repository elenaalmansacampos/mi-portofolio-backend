package com.elena.portafolio;

import com.elena.portafolio.dto.LoginResponse;
import com.elena.portafolio.service.AuthService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void loginConCredencialesValidasDevuelveTokenYRole() {
        LoginResponse response = authService.login("Elena", "REDACTED");

        assertNotNull(response.getToken());
        assertEquals("Elena", response.getUsername());
        assertEquals("ADMIN", response.getRole());
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
                authService.login("usuario-inventado", "REDACTED")
        );
    }
}
