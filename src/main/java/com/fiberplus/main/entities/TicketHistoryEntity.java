package com.fiberplus.main.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.core.ObjectReadContext.Base;

@Document(collection = "ticket_history")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TicketHistoryEntity extends Base {
     private String ticketId;
    private String action; // CREATED | MOVED | ASSIGNED | CLOSED

    private String fromColumn;
    private String toColumn;

    private String changedBy;
}
