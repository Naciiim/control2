package org.example.notification.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.notification.model.enumeration.NotificationStatus;
import org.example.notification.model.enumeration.NotificationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    private String id;
    private Long userId;
    private String message;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime timestamp;
    private LocalDateTime readAt;
}
