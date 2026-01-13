package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fiberplus.main.controllers.task.request.TaskCompletionCreateDto;
import com.fiberplus.main.dtos.TaskCompletionDto;
import com.fiberplus.main.entities.TaskCompletionEntity;
import com.fiberplus.main.entities.UserEntity;
import com.fiberplus.main.exception.ResourceNotFoundException;
import com.fiberplus.main.repositories.ITaskCompletionRepository;
import com.fiberplus.main.repositories.ITaskRepository;
import com.fiberplus.main.repositories.IUserRepository;

@Service
public class TaskCompletionService {
     private static final Logger logger = LoggerFactory.getLogger(TaskCompletionService.class);
    
    private final ITaskCompletionRepository completionRepo;
    private final ITaskRepository taskRepo;
    private final IUserRepository userRepo;

    public TaskCompletionService(ITaskCompletionRepository completionRepo, ITaskRepository taskRepo, IUserRepository userRepo) {
        this.completionRepo = completionRepo;
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    public TaskCompletionDto createCompletion(TaskCompletionCreateDto dto) {
        // Verificar que la tarea existe
        taskRepo.findById(dto.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la tarea con id ", "id", dto.getTaskId()));

        String userId = getCurrentUserId();
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        TaskCompletionEntity completion = TaskCompletionEntity.builder()
                .id(id)
                .taskId(dto.getTaskId())
                .boardId(dto.getBoardId())
                .completedBy(userId)
                .description(dto.getDescription())
                .notes(dto.getNotes())
                .imageUrls(dto.getImageUrls())
                .completedAt(dto.getCompletedAt() != null ? dto.getCompletedAt() : now)
                .createdAt(now)
                .build();

        completion = completionRepo.save(completion);
        logger.info("✅ Completación de tarea creada: {} para tarea {}", completion.getId(), dto.getTaskId());

        return toDto(completion);
    }

    public List<TaskCompletionDto> getCompletionsByTask(String taskId) {
        return completionRepo.findByTaskId(taskId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskCompletionDto> getCompletionsByBoard(String boardId) {
        return completionRepo.findByBoardId(boardId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskCompletionDto> getCompletionsByUser(String userId) {
        return completionRepo.findByCompletedBy(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskCompletionDto> getCompletionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return completionRepo.findByCompletedAtBetween(start, end)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TaskCompletionDto toDto(TaskCompletionEntity entity) {
        UserEntity user = userRepo.findById(entity.getCompletedBy()).orElse(null);
        String completedByName = user != null ? user.getName() + " " + user.getLastname() : "Usuario desconocido";

        return TaskCompletionDto.builder()
                .id(entity.getId())
                .taskId(entity.getTaskId())
                .boardId(entity.getBoardId())
                .completedBy(entity.getCompletedBy())
                .completedByName(completedByName)
                .description(entity.getDescription())
                .notes(entity.getNotes())
                .imageUrls(entity.getImageUrls())
                .completedAt(entity.getCompletedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private String getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserEntity) {
                return ((UserEntity) principal).getId();
            }
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}
