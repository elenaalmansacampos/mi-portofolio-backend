package com.elena.portafolio;

import com.elena.portafolio.entity.ContactMessage;
import com.elena.portafolio.service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(
                mailSender,
                "destino@test.com",
                "remitente@test.com"
        );
    }

    @Test
    void enviaNotificacionYAutoRespuestaConLosDestinatariosCorrectos() {
        ContactMessage message = new ContactMessage(
                "Cliente",
                "cliente@test.com",
                "Mensaje de prueba"
        );

        emailService.sendContactEmails(message);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender, times(2)).send(captor.capture());

        SimpleMailMessage notification = captor.getAllValues().get(0);
        SimpleMailMessage reply = captor.getAllValues().get(1);

        assertArrayEquals(
                new String[]{"destino@test.com"},
                notification.getTo()
        );
        assertArrayEquals(
                new String[]{"cliente@test.com"},
                reply.getTo()
        );
        assertEquals("remitente@test.com", notification.getFrom());
        assertEquals("remitente@test.com", reply.getFrom());
    }

    @Test
    void unFalloDelServidorDeCorreoNoPropagaLaExcepcion() {
        ContactMessage message = new ContactMessage(
                "Cliente",
                "cliente@test.com",
                "Mensaje de prueba"
        );

        doThrow(new MailSendException("fallo"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendContactEmails(message));
    }
}
