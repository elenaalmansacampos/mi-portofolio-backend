package com.elena.portafolio.controller;

import com.elena.portafolio.entity.ContactMessage;
import com.elena.portafolio.service.ContactService;

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
            @RequestBody ContactMessage contactMessage
    ) {

        contactService.sendMessage(contactMessage);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Gracias por contactar conmigo. He recibido tu mensaje y te responderé en breve."
                )
        );
    }
}