package com.fiberplus.main.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fiberplus.main.entities.NotificationEntity;

public interface INotificationRepository extends MongoRepository<NotificationEntity, String> {
    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    List<NotificationEntity> findByUserIdAndReadOrderByCreatedAtDesc(String userId, Boolean read);
    Long countByUserIdAndRead(String userId, Boolean read);
}
