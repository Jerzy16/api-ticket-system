package com.fiberplus.main.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fiberplus.main.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Document(collection = "notifications")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NotificationEntity extends BaseEntity {
    private String userId;
    private String title;
    private String message;
    private Boolean read;
    
    private String taskId;
    private String taskTitle;
    private NotificationType type;
    private String actionBy; 
}
