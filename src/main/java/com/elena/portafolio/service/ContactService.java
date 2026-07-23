package com.elena.portafolio.service;

import com.elena.portafolio.entity.ContactMessage;
import com.elena.portafolio.repository.ContactMessageRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class ContactService {


    private final ContactMessageRepository contactMessageRepository;
    private final JavaMailSender mailSender;


    public ContactService(
            ContactMessageRepository contactMessageRepository,
            JavaMailSender mailSender
    ) {
        this.contactMessageRepository = contactMessageRepository;
        this.mailSender = mailSender;
    }


    public ContactMessage sendMessage(ContactMessage contactMessage) {


        // Guardar mensaje en base de datos
        ContactMessage savedMessage =
                contactMessageRepository.save(contactMessage);


        // Email que recibe Elena
        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo("domingosamisa@gmail.com");

        email.setSubject(
                "Nuevo mensaje desde tu portfolio"
        );

        email.setText(
                "Nombre: "
                + contactMessage.getName()
                + "\nEmail: "
                + contactMessage.getEmail()
                + "\n\nMensaje:\n"
                + contactMessage.getMessage()
        );


        mailSender.send(email);


        // Respuesta automática al usuario
        sendAutoReply(contactMessage);


        return savedMessage;
    }



    private void sendAutoReply(
            ContactMessage contactMessage
    ) {


        SimpleMailMessage reply =
                new SimpleMailMessage();


        reply.setTo(
                contactMessage.getEmail()
        );


        reply.setSubject(
                "Hemos recibido tu mensaje"
        );


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


        mailSender.send(reply);
    }

}