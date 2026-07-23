package com.elena.portafolio.service;

import com.elena.portafolio.dto.ContactRequest;

import org.springframework.stereotype.Service;


@Service
public class EmailService {


    public void sendEmail(ContactRequest request) {

        System.out.println("Nuevo mensaje de contacto");
        System.out.println("Nombre: " + request.getName());
        System.out.println("Email: " + request.getEmail());
        System.out.println("Mensaje: " + request.getMessage());

    }
}