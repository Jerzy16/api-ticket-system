package com.fiberplus.main.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletionDto extends BaseDto {
    private String taskId;
    private String boardId;
    private String completedBy;
    private String completedByName;
    private String description;
    private String notes;
    private List<String> imageUrls;
    private LocalDateTime completedAt;
}
