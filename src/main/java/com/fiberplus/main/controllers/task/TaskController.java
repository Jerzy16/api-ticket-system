package com.fiberplus.main.controllers.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.dtos.TaskDto;
import com.fiberplus.main.services.TaskService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final TaskService _service;

    public TaskController(TaskService _service) {
        this._service = _service;
    }

    public ResponseEntity<ApiResponse<TaskDto>> create(@Valid @RequestBody TaskDto dto) {
        _service.createTask(dto);
        return ResponseBuilder.created("Tarea creada exitosamente", dto);
    }

}
