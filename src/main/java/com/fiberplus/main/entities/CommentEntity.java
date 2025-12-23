package com.fiberplus.main.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "comments")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity extends BaseEntity {
    private String ticketId;
    private String userId;
    private String message;
}
