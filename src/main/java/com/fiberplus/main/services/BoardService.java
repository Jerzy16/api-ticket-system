package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fiberplus.main.dtos.BoardDto;
import com.fiberplus.main.dtos.BoardWithTasksDto;
import com.fiberplus.main.dtos.TaskDto;
import com.fiberplus.main.dtos.UserDto;
import com.fiberplus.main.entities.BoardEntity;
import com.fiberplus.main.entities.TaskEntity;
import com.fiberplus.main.entities.UserEntity;
import com.fiberplus.main.exception.ConflictException;
import com.fiberplus.main.exception.ResourceNotFoundException;
import com.fiberplus.main.repositories.IBoardRepository;
import com.fiberplus.main.repositories.ITaskRepository;
import com.fiberplus.main.repositories.IUserRepository;

@Service
public class BoardService {

    private final IBoardRepository _repo;
    private final ITaskRepository _taskRepo;
    private final IUserRepository _userRepo;

    public BoardService(IBoardRepository _repo, ITaskRepository _taskRepo, IUserRepository _userRepo) {
        this._repo = _repo;
        this._taskRepo = _taskRepo;
        this._userRepo = _userRepo;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public BoardDto create(BoardDto dto) {
        _repo.findByTitle(dto.getTitle())
                .ifPresent(e -> {
                    throw new ConflictException(
                            "El tablero con el título '" + dto.getTitle() + "' ya existe.");
                });

        String id = UUID.randomUUID().toString();
        String userId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        BoardEntity board = BoardEntity.builder()
                .id(id)
                .title(dto.getTitle())
                .createdBy(userId)
                .status("ACTIVE")
                .createdAt(now)
                .updatedAt(now)
                .build();

        board = _repo.save(board);

        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .createdBy(board.getCreatedBy())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

    private BoardDto toDto(BoardEntity board) {
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .createdBy(board.getCreatedBy())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

    private UserDto userToDto(UserEntity user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .lastName(user.getLastname())
                .email(user.getEmail())
                .position(user.getPosition())
                .photo(user.getPhoto())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Convierte TaskEntity a TaskDto optimizado - UNA SOLA consulta para todos los usuarios
     */
    private TaskDto taskToDto(TaskEntity task) {
        List<UserDto> assignedUsers = null;
        
        if (task.getAssignedTo() != null && !task.getAssignedTo().isEmpty()) {
            // ✅ Solución: findAllById hace UNA SOLA consulta para todos los IDs
            List<UserEntity> users = _userRepo.findAllById(task.getAssignedTo());
            
            if (!users.isEmpty()) {
                assignedUsers = users.stream()
                        .map(this::userToDto)
                        .collect(Collectors.toList());
            }
        }

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .boardId(task.getBoardId())
                .assignedTo(task.getAssignedTo())
                .assignedUsers(assignedUsers)
                .dueDate(task.getDueDate())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    /**
     * Convierte TaskEntity a TaskDto usando caché de usuarios pre-cargados
     */
    private TaskDto taskToDtoWithCache(TaskEntity task, Map<String, UserDto> userMap) {
        List<UserDto> assignedUsers = null;
        
        if (task.getAssignedTo() != null && !task.getAssignedTo().isEmpty()) {
            assignedUsers = task.getAssignedTo().stream()
                    .map(userMap::get)
                    .filter(user -> user != null)
                    .collect(Collectors.toList());
        }

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .boardId(task.getBoardId())
                .assignedTo(task.getAssignedTo())
                .assignedUsers(assignedUsers)
                .dueDate(task.getDueDate())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public List<BoardDto> getAll() {
        return _repo.findAll()
                .stream()
                .filter(board -> "ACTIVE".equals(board.getStatus()))
                .map(this::toDto)
                .toList();
    }

    /**
     * Optimizado: Carga todos los boards con sus tareas y usuarios en solo 3 queries
     * Query 1: Obtener todos los boards activos
     * Query 2: Obtener todas las tareas de esos boards
     * Query 3: Obtener todos los usuarios asignados
     */
    public List<BoardWithTasksDto> getAllWithTasks() {
        // Query 1: Obtener todos los boards activos
        List<BoardEntity> boards = _repo.findAll()
                .stream()
                .filter(board -> "ACTIVE".equals(board.getStatus()))
                .toList();

        if (boards.isEmpty()) {
            return List.of();
        }

        // Extraer IDs de boards
        List<String> boardIds = boards.stream()
                .map(BoardEntity::getId)
                .toList();
        
        // Query 2: Obtener TODAS las tareas de todos los boards de una vez
        List<TaskEntity> allTasks = _taskRepo.findByBoardIdIn(boardIds);
        
        // Query 3: Obtener TODOS los usuarios únicos asignados de una vez
        List<String> allUserIds = allTasks.stream()
                .filter(task -> task.getAssignedTo() != null)
                .flatMap(task -> task.getAssignedTo().stream())
                .distinct()
                .toList();
        
        // Crear mapa de usuarios para acceso O(1)
        Map<String, UserDto> userMap = allUserIds.isEmpty() 
                ? Map.of()
                : _userRepo.findAllById(allUserIds).stream()
                    .collect(Collectors.toMap(
                        UserEntity::getId,
                        this::userToDto
                    ));

        // Agrupar tareas por boardId para acceso O(1)
        Map<String, List<TaskEntity>> tasksByBoard = allTasks.stream()
                .collect(Collectors.groupingBy(TaskEntity::getBoardId));

        // Construir DTOs
        return boards.stream()
                .map(board -> {
                    List<TaskDto> taskDtos = tasksByBoard
                            .getOrDefault(board.getId(), List.of())
                            .stream()
                            .map(task -> taskToDtoWithCache(task, userMap))
                            .collect(Collectors.toList());

                    return BoardWithTasksDto.builder()
                            .id(board.getId())
                            .title(board.getTitle())
                            .createdBy(board.getCreatedBy())
                            .status(board.getStatus())
                            .createdAt(board.getCreatedAt())
                            .updatedAt(board.getUpdatedAt())
                            .tasks(taskDtos)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Optimizado: Obtiene un board con sus tareas en solo 2-3 queries
     */
    public BoardWithTasksDto getByIdWithTasks(String id) {
        // Query 1: Obtener el board
        BoardEntity board = _repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el tablero con id ", "id", id));

        if (!"ACTIVE".equals(board.getStatus())) {
            throw new ResourceNotFoundException(
                    "El tablero con id " + id + " no está activo", "id", id);
        }

        // Query 2: Obtener todas las tareas del board
        List<TaskEntity> tasks = _taskRepo.findByBoardId(id);
        
        if (tasks.isEmpty()) {
            return BoardWithTasksDto.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .createdBy(board.getCreatedBy())
                    .status(board.getStatus())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .tasks(List.of())
                    .build();
        }

        // Query 3: Obtener todos los usuarios asignados de una vez
        List<String> allUserIds = tasks.stream()
                .filter(task -> task.getAssignedTo() != null)
                .flatMap(task -> task.getAssignedTo().stream())
                .distinct()
                .toList();
        
        Map<String, UserDto> userMap = allUserIds.isEmpty()
                ? Map.of()
                : _userRepo.findAllById(allUserIds).stream()
                    .collect(Collectors.toMap(
                        UserEntity::getId,
                        this::userToDto
                    ));

        // Construir DTOs de tareas con caché de usuarios
        List<TaskDto> taskDtos = tasks.stream()
                .map(task -> taskToDtoWithCache(task, userMap))
                .collect(Collectors.toList());

        return BoardWithTasksDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .createdBy(board.getCreatedBy())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .tasks(taskDtos)
                .build();
    }

    public BoardDto update(String id, BoardDto dto) {
        BoardEntity board = _repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el tablero con id ", "id", id));

        String userId = getCurrentUserId();

        _repo.findByTitle(dto.getTitle())
                .filter(b -> !b.getId().equals(id))
                .ifPresent(b -> {
                    throw new ConflictException(
                            "Ya existe un tablero con el título " + dto.getTitle());
                });

        BoardEntity updated = BoardEntity.builder()
                .id(board.getId())
                .title(dto.getTitle())
                .createdBy(board.getCreatedBy()) // ✅ Corregido: mantener el creador original
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        updated = _repo.save(updated);

        return toDto(updated);
    }

    public void delete(String id) {
        BoardEntity board = _repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el tablero con id ", "id", id));

        board.setStatus("INACTIVE");
        board.setUpdatedAt(LocalDateTime.now());

        _repo.save(board);
    }
}