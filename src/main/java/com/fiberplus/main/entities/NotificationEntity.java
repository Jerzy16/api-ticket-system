package com.fiberplus.main.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "notifications")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEntity extends BaseEntity {
    private String userId;
    private String title;
    private String message;

    private Boolean read;
}
