package com.elena.portafolio.dto;

import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String title,
        String slug,
        String description,
        String technologies,
        String imageUrl,
        String githubUrl,
        String demoUrl,
        boolean featured,
        int displayOrder,
        LocalDateTime createdAt
) {
}
