package com.elena.portafolio.service;

import com.elena.portafolio.entity.ContactMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String notificationEmail;
    private final String fromEmail;


    public EmailService(
            JavaMailSender mailSender,
            @Value("${contact.notification-email:elenaalmansacampos@gmail.com}")
            String notificationEmail,
            @Value("${contact.from-email:no-reply@localhost}")
            String fromEmail
    ) {
        this.mailSender = mailSender;
        this.notificationEmail = notificationEmail;
        this.fromEmail = fromEmail;
    }


    @Async
    public void sendContactEmails(ContactMessage contactMessage) {

        SimpleMailMessage notification = new SimpleMailMessage();

        notification.setFrom(fromEmail);
        notification.setTo(notificationEmail);
        notification.setSubject("Nuevo mensaje desde tu portfolio");
        notification.setText(
                "Nombre: "
                + contactMessage.getName()
                + "\nEmail: "
                + contactMessage.getEmail()
                + "\n\nMensaje:\n"
                + contactMessage.getMessage()
        );

        sendSafely(notification);

        SimpleMailMessage reply = new SimpleMailMessage();

        reply.setFrom(fromEmail);
        reply.setTo(contactMessage.getEmail());
        reply.setSubject("Hemos recibido tu mensaje");
        reply.setText(
                "Hola "
                + contactMessage.getName()
                + ",\n\n"
                + "Muchas gracias por ponerte en contacto conmigo.\n\n"
                + "He recibido tu mensaje y revisaré tu consulta lo antes posible.\n"
                + "Me pondré en contacto contigo próximamente.\n\n"
                + "Un saludo,\n"
                + "Elena"
        );

        sendSafely(reply);
    }


    private void sendSafely(SimpleMailMessage message) {

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            log.warn("No se pudo enviar el correo de contacto", exception);
        }
    }
}
