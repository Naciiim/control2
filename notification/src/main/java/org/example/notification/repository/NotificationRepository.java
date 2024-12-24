package org.example.notification.repository;

import org.example.notification.model.entity.Notification;
import org.example.notification.model.enumeration.NotificationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdOrderByTimestampDesc(Long userId);
    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);
}