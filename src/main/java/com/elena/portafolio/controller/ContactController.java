package com.elena.portafolio.controller;


import com.elena.portafolio.dto.ContactRequest;
import com.elena.portafolio.service.EmailService;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/contact")
public class ContactController {


    private final EmailService emailService;


    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }


    @PostMapping
    public String sendContact(
            @RequestBody ContactRequest request
    ) {

        emailService.sendEmail(request);

        return "Les responderemos en breve";
    }
}