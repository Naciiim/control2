package org.example.notification.event;

import com.example.notification.event.OrderEvent;
import com.example.notification.service.NotificationService;
import com.example.notification.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventConsumer {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(
        topics = "order-events",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleOrderEvent(OrderEvent event) {
        log.info("Réception d'un événement de commande: {}", event);
        
        try {
            Notification notification = Notification.builder()
                .userId(event.getUserId())
                .type(NotificationType.ORDER_CREATED)
                .message(createNotificationMessage(event))
                .status(NotificationStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .build();

            notificationService.processNewNotification(notification);
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement de commande", e);
        }
    }

    private String createNotificationMessage(OrderEvent event) {
        return String.format("Votre commande #%d a été %s", 
            event.getOrderId(), 
            event.getStatus().toLowerCase());
    }
}