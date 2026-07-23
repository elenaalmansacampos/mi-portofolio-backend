package com.elena.portafolio.security;

import com.elena.portafolio.security.filter.JWTAuthentication;
import com.elena.portafolio.security.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JWTAuthentication jwtAuthentication;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JWTAuthentication jwtAuthentication) {

        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthentication = jwtAuthentication;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // Desactivar CSRF porque usamos JWT
                .csrf(csrf -> csrf.disable())

                // Permitir CORS
                .cors(Customizer.withDefaults())

                // Proveedor de autenticación
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

                        // Login público
                        .requestMatchers("/api/auth/login")
                        .permitAll()

                        // Contacto público
                        .requestMatchers(
                                "/api/contact",
                                "/api/contact/**")
                        .permitAll()

                        // Error público
                        .requestMatchers("/error")
                        .permitAll()

                        // SOLO PARA LA PRUEBA
                        .requestMatchers(
                                "/api/projects",
                                "/api/projects/**")
                        .permitAll()

                        // El resto necesita autenticación
                        .anyRequest()
                        .authenticated()
                )

                // Filtro JWT
                .addFilterBefore(
                        jwtAuthentication,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
}