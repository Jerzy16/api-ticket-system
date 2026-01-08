package com.fiberplus.main.dtos;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TaskUpdateDto extends BaseDto {
    @NotBlank(message = "El título de la tarea no puede estar vacío.")
    private String title;
    
    private String description;
    
    private List<String> assignedTo;
    
    private String priority; 
    
    private LocalDateTime dueDate;
    
    private String latitude;
    
    private String longitude;
    
    private String boardId;
    
    private String status;
    
    private Integer position;
}
