package com.fiberplus.main.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskMoveDto {
    @NotBlank(message = "El ID del tablero destino es requerido")
    private String toBoardId;
    
    private Integer position;
}
