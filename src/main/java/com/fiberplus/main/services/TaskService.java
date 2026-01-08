package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fiberplus.main.dtos.TaskDto;
import com.fiberplus.main.entities.TaskEntity;
import com.fiberplus.main.repositories.ITaskRepository;

@Service
public class TaskService {
    private ITaskRepository _repo;

    public TaskService(ITaskRepository _repo) {
        this._repo = _repo;
    }

    public TaskDto createTask(TaskDto dto) {
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        List<String> assignedUserIds = dto.getAssignedTo();

        TaskEntity task = TaskEntity.builder()
                .id(id)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .boardId(dto.getBoardId())
                .assignedTo(assignedUserIds)
                .dueDate(dto.getDueDate())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        task = _repo.save(task);

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .boardId(task.getBoardId())
                .assignedTo(task.getAssignedTo()) 
                .dueDate(task.getDueDate())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}