package com.fiberplus.main.controllers.task;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.dtos.TaskDto;
import com.fiberplus.main.dtos.TaskMoveDto;
import com.fiberplus.main.dtos.TaskUpdateDto;
import com.fiberplus.main.services.TaskService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("api/tasks")
@Tag(name = "Tasks", description = "API para gestión de tareas")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva tarea")
    public ResponseEntity<ApiResponse<TaskDto>> createTask(@Valid @RequestBody TaskDto dto) {
        TaskDto task = service.createTask(dto);
        return ResponseBuilder.created("Tarea creada exitosamente.", task);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Actualizar una tarea",
        description = "Actualiza los campos de una tarea. Se pueden actualizar uno o varios campos."
    )
    public ResponseEntity<ApiResponse<TaskDto>> updateTask(
            @Parameter(description = "ID de la tarea a actualizar", required = true)
            @PathVariable String id,
            @Valid @RequestBody TaskUpdateDto updateDto) {
        TaskDto updatedTask = service.updateTask(id, updateDto);
        return ResponseBuilder.ok("Tarea actualizada exitosamente", updatedTask);
    }

    @PatchMapping("/{id}/move")
    @Operation(
        summary = "Mover tarea a otro tablero",
        description = "Actualiza el boardId de una tarea para moverla a otro estado"
    )
    public ResponseEntity<ApiResponse<TaskDto>> moveTask(
            @Parameter(description = "ID de la tarea a mover", required = true)
            @PathVariable String id,
            @Valid @RequestBody TaskMoveDto moveDto) {
        TaskDto movedTask = service.moveTask(id, moveDto);
        return ResponseBuilder.ok("Tarea movida exitosamente", movedTask);
    }
}