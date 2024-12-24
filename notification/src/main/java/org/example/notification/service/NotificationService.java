package org.example.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.example.notification.model.entity.Notification;
import org.example.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private EmailService emailService;

    public void processNewNotification(Notification notification) {
        try {
            // Sauvegarder la notification
            notification.setStatus(NotificationStatus.PENDING);
            notification.setTimestamp(LocalDateTime.now());
            notification = notificationRepository.save(notification);

            // Envoyer la notification par email
            emailService.sendNotificationEmail(notification);

            // Mettre à jour le statut
            notification.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notification);
            
            log.info("Notification traitée avec succès: {}", notification.getId());
        } catch (Exception e) {
            log.error("Erreur lors du traitement de la notification", e);
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
        }
    }

    public List<NotificationResponse> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
            .findByUserIdOrderByTimestampDesc(userId);
        
        return notifications.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId)
            .ifPresent(notification -> {
                notification.setStatus(NotificationStatus.READ);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
            });
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .message(notification.getMessage())
            .type(notification.getType())
            .status(notification.getStatus())
            .timestamp(notification.getTimestamp())
            .readAt(notification.getReadAt())
            .build();
    }
}