package com.elena.portafolio;

import com.elena.portafolio.entity.User;
import com.elena.portafolio.repository.RefreshTokenRepository;
import com.elena.portafolio.repository.UserRepository;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    private static final String PASSWORD = "test-password";
    private static final String LOGIN_BODY = """
            {"username":"Elena","password":"test-password"}
            """;

    @Autowired
    private MockMvc mockMvc;

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
    void loginDevuelveAccessTokenYCookieHttpOnly() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", "198.51.100.21")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(header().string(
                        HttpHeaders.SET_COOKIE,
                        containsString("refresh_token=")
                ))
                .andExpect(header().string(
                        HttpHeaders.SET_COOKIE,
                        containsString("HttpOnly")
                ))
                .andReturn();

        assertNotNull(extractRefreshCookie(result));
    }

    @Test
    void refreshRotaLaCookieYRechazaLaAnterior() throws Exception {
        Cookie firstCookie = loginAndGetCookie();

        MvcResult refreshed = mockMvc.perform(post("/api/auth/refresh")
                        .cookie(firstCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        Cookie secondCookie = extractRefreshCookie(refreshed);

        assertNotNull(secondCookie);
        assertNotEquals(firstCookie.getValue(), secondCookie.getValue());

        mockMvc.perform(post("/api/auth/refresh").cookie(firstCookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRevocaLaCookieYRechazaRefresh() throws Exception {
        Cookie cookie = loginAndGetCookie();

        mockMvc.perform(post("/api/auth/logout").cookie(cookie))
                .andExpect(status().isNoContent())
                .andExpect(header().string(
                        HttpHeaders.SET_COOKIE,
                        containsString("Max-Age=0")
                ));

        mockMvc.perform(post("/api/auth/refresh").cookie(cookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshSinCookieDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    private Cookie loginAndGetCookie() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", "198.51.100.22")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = extractRefreshCookie(result);

        assertNotNull(cookie);

        return cookie;
    }

    private Cookie extractRefreshCookie(MvcResult result) {
        Cookie cookie = result.getResponse().getCookie("refresh_token");

        if (cookie != null) {
            return cookie;
        }

        String header = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);

        if (header == null) {
            return null;
        }

        String value = header.split(";", 2)[0].split("=", 2)[1];

        return new Cookie("refresh_token", value);
    }
}
