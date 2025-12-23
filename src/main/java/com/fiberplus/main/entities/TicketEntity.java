package com.fiberplus.main.entities;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "tickets")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TicketEntity extends BaseEntity {
    private String code; // TCK-0001
    private String title;
    private String description;

    private String priority; // LOW | MEDIUM | HIGH | CRITICAL
    private String status; // OPEN | IN_PROGRESS | DONE | CLOSED

    private String boardId;
    private String columnId;
    private String zoneId;

    private String assignedTo; // técnico
    private String reportedBy; // operador

    private Location location;

    private LocalDateTime scheduledAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}
