package com.fiberplus.main.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Document(collection = "tasks")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TaskEntity extends BaseEntity {
    private String title;
    private String description;
    private String priority;
    private String boardId;
    private List<String> assignedTo;
    private LocalDateTime dueDate;
}
