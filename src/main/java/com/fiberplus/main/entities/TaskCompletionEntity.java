package com.fiberplus.main.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Document(collection = "task_completions")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletionEntity extends BaseEntity {
    private String taskId;
    private String boardId;
    private String completedBy;
    private String description;
    private String notes;
    private List<String> imageUrls;
    private LocalDateTime completedAt;
}
