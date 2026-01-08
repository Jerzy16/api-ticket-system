package com.fiberplus.main.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardWithTasksDto {
    private String id;
    private String title;
    private String createdBy;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskDto> tasks;    
}
