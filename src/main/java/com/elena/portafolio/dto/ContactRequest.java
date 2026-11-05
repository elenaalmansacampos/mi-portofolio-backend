package com.elena.portafolio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String name,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido")
        @Size(max = 254, message = "El email no puede superar 254 caracteres")
        String email,

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(min = 10, max = 5000,
                message = "El mensaje debe tener entre 10 y 5000 caracteres")
        String message
) {
}
