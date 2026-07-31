package com.elena.portafolio;

import com.elena.portafolio.entity.ContactMessage;
import com.elena.portafolio.repository.ContactMessageRepository;
import com.elena.portafolio.service.ContactService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactMessageRepository contactMessageRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ContactService contactService;

    @Test
    void enviarMensajeGuardaEnBDYEnviaDosEmails() {
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
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void enviarMensajeEnviaEmailDeNotificacionALaCuentaDeElena() {
        ContactMessage message = new ContactMessage(
                "Cliente",
                "cliente@test.com",
                "Mensaje de prueba"
        );

        when(contactMessageRepository.save(any(ContactMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        contactService.sendMessage(message);

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }
}
