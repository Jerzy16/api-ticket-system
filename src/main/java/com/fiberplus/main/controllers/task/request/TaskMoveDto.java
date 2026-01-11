package com.fiberplus.main.controllers.task.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskMoveDto {
    @NotBlank(message = "El board de origen es requerido")
    private String fromBoardId;
    
    @NotBlank(message = "El board de destino es requerido")
    private String toBoardId;
    
    @NotNull(message = "La nueva posición es requerida")
    private Integer newIndex = 0;
}
