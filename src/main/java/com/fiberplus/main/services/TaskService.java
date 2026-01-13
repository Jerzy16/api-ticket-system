package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fiberplus.main.dtos.TaskDto;
import com.fiberplus.main.dtos.TaskUpdateDto;
import com.fiberplus.main.entities.TaskEntity;
import com.fiberplus.main.entities.UserEntity;
import com.fiberplus.main.enums.NotificationType;
import com.fiberplus.main.exception.ResourceNotFoundException;
import com.fiberplus.main.repositories.IBoardRepository;
import com.fiberplus.main.repositories.ITaskRepository;
import com.fiberplus.main.repositories.IUserRepository;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final ITaskRepository taskRepo;
    private final IBoardRepository boardRepo;
    private final IUserRepository userRepo;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public TaskService(ITaskRepository taskRepo, IBoardRepository boardRepo, IUserRepository userRepo,
            NotificationService notificationService, EmailService emailService) {
        this.taskRepo = taskRepo;
        this.boardRepo = boardRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    public TaskDto createTask(TaskDto dto) {
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        List<String> assignedUserIds = dto.getAssignedTo() != null ? dto.getAssignedTo() : new ArrayList<>();

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

        task = taskRepo.save(task);
        logger.info("✅ Tarea creada: {} en board {}", task.getId(), task.getBoardId());

        String currentUserId = getCurrentUserId();

        for (String userId : assignedUserIds) {
            try {
                UserEntity user = userRepo.findById(userId).orElse(null);
                if (user != null) {
                    notificationService.createAndSendNotification(
                            userId,
                            "Nueva tarea asignada",
                            "Se te ha asignado la tarea: " + task.getTitle(),
                            task.getId(),
                            task.getTitle(),
                            NotificationType.TASK_ASSIGNED,
                            currentUserId);

                    emailService.sendTaskAssignmentEmail(
                            user.getEmail(),
                            user.getName(),
                            task.getTitle(),
                            task.getDescription(),
                            task.getPriority(),
                            task.getDueDate());
                }
            } catch (Exception e) {
                logger.error("Error enviando notificación/email a usuario {}: {}", userId, e.getMessage());
            }
        }

        return entityToDto(task);
    }

    public TaskDto updateTask(String taskId, TaskUpdateDto updateDto) {
        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la tarea con id ", "id", taskId));

        StringBuilder changes = new StringBuilder();
        List<String> previousAssignedUsers = new ArrayList<>(
                task.getAssignedTo() != null ? task.getAssignedTo() : new ArrayList<>());

        if (updateDto.getTitle() != null && !updateDto.getTitle().equals(task.getTitle())) {
            changes.append("Título actualizado. ");
            task.setTitle(updateDto.getTitle());
        }

        if (updateDto.getDescription() != null && !updateDto.getDescription().equals(task.getDescription())) {
            changes.append("Descripción actualizada. ");
            task.setDescription(updateDto.getDescription());
        }

        if (updateDto.getPriority() != null && !updateDto.getPriority().equals(task.getPriority())) {
            changes.append("Prioridad cambiada a " + updateDto.getPriority() + ". ");
            task.setPriority(updateDto.getPriority());
        }

        if (updateDto.getBoardId() != null && !updateDto.getBoardId().equals(task.getBoardId())) {
            boardRepo.findById(updateDto.getBoardId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró el tablero con id ", "id", updateDto.getBoardId()));
            changes.append("Tablero actualizado. ");
            task.setBoardId(updateDto.getBoardId());
        }

        if (updateDto.getDueDate() != null && !updateDto.getDueDate().equals(task.getDueDate())) {
            changes.append("Fecha de vencimiento actualizada. ");
            task.setDueDate(updateDto.getDueDate());
        }

        if (updateDto.getLatitude() != null) {
            task.setLatitude(updateDto.getLatitude());
        }

        if (updateDto.getLongitude() != null) {
            task.setLongitude(updateDto.getLongitude());
        }

        if (updateDto.getAssignedTo() != null) {
            List<String> newAssignedUsers = updateDto.getAssignedTo();

            List<String> newlyAssigned = newAssignedUsers.stream()
                    .filter(userId -> !previousAssignedUsers.contains(userId))
                    .collect(Collectors.toList());

            List<String> removedUsers = previousAssignedUsers.stream()
                    .filter(userId -> !newAssignedUsers.contains(userId))
                    .collect(Collectors.toList());

            if (!newlyAssigned.isEmpty() || !removedUsers.isEmpty()) {
                changes.append("Asignaciones actualizadas. ");
                task.setAssignedTo(newAssignedUsers);
            }

            String currentUserId = getCurrentUserId();

            for (String userId : newlyAssigned) {
                try {
                    UserEntity user = userRepo.findById(userId).orElse(null);
                    if (user != null) {
                        notificationService.createAndSendNotification(
                                userId,
                                "Nueva tarea asignada",
                                "Se te ha asignado la tarea: " + task.getTitle(),
                                task.getId(),
                                task.getTitle(),
                                NotificationType.TASK_ASSIGNED,
                                currentUserId);

                        emailService.sendTaskAssignmentEmail(
                                user.getEmail(),
                                user.getName(),
                                task.getTitle(),
                                task.getDescription(),
                                task.getPriority(),
                                task.getDueDate());
                    }
                } catch (Exception e) {
                    logger.error("Error enviando notificación/email a usuario {}: {}", userId, e.getMessage());
                }
            }
        }

        task.setUpdatedAt(LocalDateTime.now());
        task = taskRepo.save(task);
        logger.info("✅ Tarea actualizada: {}", task.getId());

        if (changes.length() > 0) {
            String currentUserId = getCurrentUserId();
            List<String> currentAssignedUsers = task.getAssignedTo() != null ? task.getAssignedTo() : new ArrayList<>();

            for (String userId : currentAssignedUsers) {
                try {
                    UserEntity user = userRepo.findById(userId).orElse(null);
                    if (user != null && !userId.equals(currentUserId)) {
                        notificationService.createAndSendNotification(
                                userId,
                                "Tarea actualizada",
                                "La tarea '" + task.getTitle() + "' ha sido actualizada",
                                task.getId(),
                                task.getTitle(),
                                NotificationType.TASK_UPDATED,
                                currentUserId);

                        emailService.sendTaskUpdateEmail(
                                user.getEmail(),
                                user.getName(),
                                task.getTitle(),
                                changes.toString());
                    }
                } catch (Exception e) {
                    logger.error("Error enviando notificación/email a usuario {}: {}", userId, e.getMessage());
                }
            }
        }

        return entityToDto(task);
    }

    public TaskDto moveTask(String taskId, String fromBoardId, String toBoardId, Integer newIndex) {
        logger.info("🔄 Moviendo tarea {} de board {} a board {} (índice: {})",
                taskId, fromBoardId, toBoardId, newIndex);

        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la tarea con id ", "id", taskId));

        boardRepo.findById(toBoardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el tablero con id ", "id", toBoardId));

        task.setBoardId(toBoardId);
        task.setUpdatedAt(LocalDateTime.now());

        task = taskRepo.save(task);

        logger.info("✅ Tarea {} movida exitosamente al board {}", taskId, toBoardId);

        String currentUserId = getCurrentUserId();
        List<String> assignedUsers = task.getAssignedTo() != null ? task.getAssignedTo() : new ArrayList<>();

        for (String userId : assignedUsers) {
            try {
                UserEntity user = userRepo.findById(userId).orElse(null);
                if (user != null && !userId.equals(currentUserId)) {
                    notificationService.createAndSendNotification(
                            userId,
                            "Tarea movida",
                            "La tarea '" + task.getTitle() + "' ha sido movida a otro tablero",
                            task.getId(),
                            task.getTitle(),
                            NotificationType.TASK_MOVED,
                            currentUserId);

                    emailService.sendTaskMovedEmail(
                            user.getEmail(),
                            user.getName(),
                            task.getTitle(),
                            task.getBoardId(),
                            toBoardId);
                }
            } catch (Exception e) {
                logger.error("Error enviando notificación/email a usuario {}: {}", userId, e.getMessage());
            }
        }

        return entityToDto(task);
    }

    private String getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserEntity) {
                return ((UserEntity) principal).getId();
            }
            return "system";
        } catch (Exception e) {
            return "system";
        }
    }

    private TaskDto entityToDto(TaskEntity task) {
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