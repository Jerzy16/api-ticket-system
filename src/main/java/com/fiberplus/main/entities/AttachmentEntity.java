package com.fiberplus.main.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "attachments")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentEntity extends BaseEntity {
    
    private String ticketId;
    private String type; // IMAGE | PDF | VIDEO
    private String url;

    private String uploadedBy;
}
