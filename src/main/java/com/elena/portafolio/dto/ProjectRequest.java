package com.elena.portafolio.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 120, message = "El título no puede superar 120 caracteres")
        String title,

        @Size(max = 160, message = "El slug no puede superar 160 caracteres")
        @Pattern(
                regexp = "^$|^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "El slug solo admite minúsculas, números y guiones"
        )
        String slug,

        @Size(max = 5000, message = "La descripción no puede superar 5000 caracteres")
        String description,

        @Size(max = 500, message = "Las tecnologías no pueden superar 500 caracteres")
        String technologies,

        @Size(max = 500, message = "La URL de imagen no puede superar 500 caracteres")
        String imageUrl,

        @Size(max = 500, message = "La URL de GitHub no puede superar 500 caracteres")
        String githubUrl,

        @Size(max = 500, message = "La URL de demo no puede superar 500 caracteres")
        String demoUrl,

        Boolean featured,

        @Min(value = 0, message = "El orden no puede ser negativo")
        @Max(value = 9999, message = "El orden es demasiado alto")
        Integer displayOrder
) {
}
