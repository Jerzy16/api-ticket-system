package com.fiberplus.main.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "ticket_history")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TicketHistoryEntity{
     private String ticketId;
    private String action; 

    private String fromColumn;
    private String toColumn;

    private String changedBy;
}
