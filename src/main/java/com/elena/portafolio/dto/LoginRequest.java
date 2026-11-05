package com.elena.portafolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 100, message = "El usuario no puede superar 100 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 200, message = "La contraseña no puede superar 200 caracteres")
    private String password;


    public LoginRequest() {
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }
}