package com.fiberplus.main.dtos;

import com.fiberplus.main.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NotificationDto extends BaseDto {
    private String userId;
    private String title;
    private String message;
    private Boolean read;
    private String taskId;
    private String taskTitle;
    private NotificationType type;
    private String actionBy;
}
