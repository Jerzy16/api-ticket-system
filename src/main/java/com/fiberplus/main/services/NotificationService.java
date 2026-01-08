package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fiberplus.main.dtos.NotificationDto;
import com.fiberplus.main.entities.NotificationEntity;
import com.fiberplus.main.enums.NotificationType;
import com.fiberplus.main.exception.ResourceNotFoundException;
import com.fiberplus.main.repositories.INotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final INotificationRepository notificationRepo;
    private final SimpMessagingTemplate messagingTemplate;
    
    public NotificationDto createAndSendNotification(
            String userId, 
            String title, 
            String message,
            String taskId,
            String taskTitle,
            NotificationType type,
            String actionBy) {
        
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        
        NotificationEntity notification = NotificationEntity.builder()
                .id(id)
                .userId(userId)
                .title(title)
                .message(message)
                .read(false)
                .taskId(taskId)
                .taskTitle(taskTitle)
                .type(type)
                .actionBy(actionBy)
                .build();
        
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        notification = notificationRepo.save(notification);
        
        NotificationDto dto = entityToDto(notification);
        
        try {
            messagingTemplate.convertAndSendToUser(
                    userId, 
                    "/queue/notifications", 
                    dto
            );
        } catch (Exception e) {
            System.err.println("Error enviando notificación por WebSocket: " + e.getMessage());
        }
        
        return dto;
    }
    
    public List<NotificationDto> getUserNotifications(String userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
    
    public List<NotificationDto> getUnreadNotifications(String userId) {
        return notificationRepo.findByUserIdAndReadOrderByCreatedAtDesc(userId, false)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount(String userId) {
        return notificationRepo.countByUserIdAndRead(userId, false);
    }
    
    public NotificationDto markAsRead(String notificationId) {
        NotificationEntity notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la notificación con id ", "id", notificationId));
        
        notification.setRead(true);
        notification.setUpdatedAt(LocalDateTime.now());
        notification = notificationRepo.save(notification);
        
        return entityToDto(notification);
    }
    
    public void markAllAsRead(String userId) {
        List<NotificationEntity> notifications = 
                notificationRepo.findByUserIdAndReadOrderByCreatedAtDesc(userId, false);
        
        LocalDateTime now = LocalDateTime.now();
        notifications.forEach(notification -> {
            notification.setRead(true);
            notification.setUpdatedAt(now);
        });
        
        notificationRepo.saveAll(notifications);
    }
    
    public void deleteNotification(String notificationId) {
        NotificationEntity notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la notificación con id ", "id", notificationId));
        
        notificationRepo.delete(notification);
    }
    
    public void deleteReadNotifications(String userId) {
        List<NotificationEntity> readNotifications = 
                notificationRepo.findByUserIdAndReadOrderByCreatedAtDesc(userId, true);
        
        notificationRepo.deleteAll(readNotifications);
    }
    
    private NotificationDto entityToDto(NotificationEntity entity) {
        return NotificationDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .read(entity.getRead())
                .taskId(entity.getTaskId())
                .taskTitle(entity.getTaskTitle())
                .type(entity.getType())
                .actionBy(entity.getActionBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
