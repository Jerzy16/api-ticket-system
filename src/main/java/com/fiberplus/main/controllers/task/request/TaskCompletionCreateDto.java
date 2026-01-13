package com.fiberplus.main.controllers.task.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class TaskCompletionCreateDto {
    @NotBlank(message = "El ID de la tarea es requerido")
    private String taskId;
    
    @NotBlank(message = "El ID del tablero es requerido")
    private String boardId;
    
    @NotBlank(message = "La descripción es requerida")
    private String description;
    
    private String notes;
    
    @NotEmpty(message = "Debe proporcionar al menos una imagen de evidencia")
    private List<String> imageUrls;
    
    private LocalDateTime completedAt;
}
