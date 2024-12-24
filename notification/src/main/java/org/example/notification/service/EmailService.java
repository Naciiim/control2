package org.example.notification.service;


import lombok.extern.slf4j.Slf4j;
import org.example.notification.model.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    public void sendNotificationEmail(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        // Configuration de l'email à implémenter selon les besoins
        message.setTo("user@example.com"); // À récupérer depuis un service utilisateur
        message.setSubject("Nouvelle notification");
        message.setText(notification.getMessage());
        
        try {
            mailSender.send(message);
            log.info("Email envoyé avec succès pour la notification: {}", notification.getId());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email", e);
            throw e;
        }
    }
}


