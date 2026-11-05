package com.elena.portafolio.config;

import com.elena.portafolio.entity.User;
import com.elena.portafolio.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.admin.bootstrap", havingValue = "true")
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationContext context;
    private final String username;
    private final String password;


    public AdminBootstrapRunner(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ApplicationContext context,
            @Value("${app.admin.username:}") String username,
            @Value("${app.admin.password:}") String password
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.context = context;
        this.username = username;
        this.password = password;
    }


    @Override
    public void run(ApplicationArguments args) {

        int exitCode = 0;

        try {
            if (username == null || username.isBlank()) {
                throw new IllegalStateException(
                        "APP_ADMIN_USERNAME es obligatorio"
                );
            }

            if (password == null || password.length() < 12) {
                throw new IllegalStateException(
                        "APP_ADMIN_PASSWORD debe tener al menos 12 caracteres"
                );
            }

            if (userRepository.findByUsername(username.trim()).isPresent()) {
                log.info(
                        "El usuario '{}' ya existe; no se modifica",
                        username
                );
            } else {
                userRepository.save(new User(
                        username.trim(),
                        passwordEncoder.encode(password),
                        "ADMIN"
                ));
                log.info("Usuario ADMIN '{}' creado", username);
            }
        } catch (RuntimeException exception) {
            log.error(
                    "No se pudo provisionar el administrador: {}",
                    exception.getMessage()
            );
            exitCode = 1;
        }

        int finalExitCode = exitCode;
        System.exit(SpringApplication.exit(context, () -> finalExitCode));
    }
}
