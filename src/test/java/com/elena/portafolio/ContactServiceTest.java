package com.elena.portafolio;

import com.elena.portafolio.entity.ContactMessage;
import com.elena.portafolio.repository.ContactMessageRepository;
import com.elena.portafolio.service.ContactService;
import com.elena.portafolio.service.EmailService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactMessageRepository contactMessageRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ContactService contactService;

    @Test
    void guardaElMensajeYDelegaElEnvioDeCorreos() {
        ContactMessage message = new ContactMessage(
                "Elena",
                "cliente@test.com",
                "Hola, me interesa tu trabajo"
        );

        when(contactMessageRepository.save(any(ContactMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ContactMessage saved = contactService.sendMessage(message);

        assertNotNull(saved);
        assertEquals("Elena", saved.getName());

        verify(contactMessageRepository, times(1)).save(message);
        verify(emailService, times(1)).sendContactEmails(saved);
    }
}
