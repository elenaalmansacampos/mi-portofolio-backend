package com.elena.portafolio.service;

import com.elena.portafolio.entity.ContactMessage;
import com.elena.portafolio.repository.ContactMessageRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;
    private final EmailService emailService;


    public ContactService(
            ContactMessageRepository contactMessageRepository,
            EmailService emailService
    ) {
        this.contactMessageRepository = contactMessageRepository;
        this.emailService = emailService;
    }


    @Transactional
    public ContactMessage sendMessage(ContactMessage contactMessage) {

        ContactMessage savedMessage =
                contactMessageRepository.save(contactMessage);

        emailService.sendContactEmails(savedMessage);

        return savedMessage;
    }
}
