package com.elena.portafolio.repository;

import com.elena.portafolio.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository 
        extends JpaRepository<ContactMessage, Long> {
}