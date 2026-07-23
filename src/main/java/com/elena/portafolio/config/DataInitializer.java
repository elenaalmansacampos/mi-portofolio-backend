package com.elena.portafolio.config;

import com.elena.portafolio.entity.User;
import com.elena.portafolio.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DataInitializer implements CommandLineRunner {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) {


        if(userRepository.count() == 0) {


            User admin = new User(
                    "Elena",
                    passwordEncoder.encode("exit"),
                    "ADMIN"
            );


            userRepository.save(admin);

            System.out.println("Usuario ADMIN creado");
        }

    }
}
