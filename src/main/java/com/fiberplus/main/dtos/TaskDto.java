package com.fiberplus.main.dtos;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;
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
public class TaskDto extends BaseDto {
    @NotNull(message = "El campo titulo es obligatorio")
    private String title;
    @NotNull(message = "La descripcion es obligatoria")
    private String description;
    @NotNull(message = "La prioridad es obligatoria")
    private String priority;
    @NotNull(message = "La pizarra es obligatoria")
    private String boardId;
    private List<UserDto> assignedTo;
    private LocalDateTime dueDate;
}
