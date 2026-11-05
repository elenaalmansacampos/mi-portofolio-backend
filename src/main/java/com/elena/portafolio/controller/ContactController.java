package com.elena.portafolio.controller;

import com.elena.portafolio.dto.ContactRequest;
import com.elena.portafolio.entity.ContactMessage;
import com.elena.portafolio.service.ContactService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(
            @Valid @RequestBody ContactRequest request
    ) {

        ContactMessage contactMessage = new ContactMessage(
                request.name(),
                request.email(),
                request.message()
        );

        contactService.sendMessage(contactMessage);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Gracias por contactar conmigo. He recibido tu mensaje y te responderé en breve."
                )
        );
    }
}