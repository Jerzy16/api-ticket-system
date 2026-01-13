package com.fiberplus.main.controllers.task;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.controllers.task.request.TaskCompletionCreateDto;
import com.fiberplus.main.dtos.TaskCompletionDto;
import com.fiberplus.main.services.TaskCompletionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/task-completions")
@Tag(name = "Task Completions", description = "API para gestión de completaciones de tareas con evidencias")
public class TaskCompletionController {
    private final TaskCompletionService completionService;
    
    public TaskCompletionController(TaskCompletionService completionService) {
        this.completionService = completionService;
    }

    @PostMapping
    @Operation(summary = "Registrar completación de tarea", description = "Registra la completación de una tarea con evidencias")
    public ResponseEntity<ApiResponse<TaskCompletionDto>> createCompletion(
            @Valid @RequestBody TaskCompletionCreateDto dto) {
        TaskCompletionDto completion = completionService.createCompletion(dto);
        return ResponseBuilder.created("Completación registrada exitosamente", completion);
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Obtener completaciones por tarea", description = "Obtiene todas las completaciones de una tarea específica")
    public ResponseEntity<ApiResponse<List<TaskCompletionDto>>> getByTask(@PathVariable String taskId) {
        List<TaskCompletionDto> completions = completionService.getCompletionsByTask(taskId);
        return ResponseBuilder.ok("Completaciones obtenidas exitosamente", completions);
    }

    @GetMapping("/board/{boardId}")
    @Operation(summary = "Obtener completaciones por tablero", description = "Obtiene todas las completaciones de un tablero")
    public ResponseEntity<ApiResponse<List<TaskCompletionDto>>> getByBoard(@PathVariable String boardId) {
        List<TaskCompletionDto> completions = completionService.getCompletionsByBoard(boardId);
        return ResponseBuilder.ok("Completaciones obtenidas exitosamente", completions);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener completaciones por usuario", description = "Obtiene todas las completaciones de un usuario")
    public ResponseEntity<ApiResponse<List<TaskCompletionDto>>> getByUser(@PathVariable String userId) {
        List<TaskCompletionDto> completions = completionService.getCompletionsByUser(userId);
        return ResponseBuilder.ok("Completaciones obtenidas exitosamente", completions);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Obtener completaciones por rango de fechas", description = "Obtiene completaciones en un rango de fechas")
    public ResponseEntity<ApiResponse<List<TaskCompletionDto>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<TaskCompletionDto> completions = completionService.getCompletionsByDateRange(start, end);
        return ResponseBuilder.ok("Completaciones obtenidas exitosamente", completions);
    }
}
