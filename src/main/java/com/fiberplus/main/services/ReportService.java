package com.fiberplus.main.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fiberplus.main.dtos.report.AvailableBoard;
import com.fiberplus.main.dtos.report.AvailableUser;
import com.fiberplus.main.dtos.report.BoardStatistics;
import com.fiberplus.main.dtos.report.ReportDto;
import com.fiberplus.main.dtos.report.ReportSummary;
import com.fiberplus.main.dtos.report.ReportTaskDetail;
import com.fiberplus.main.dtos.report.UserPerformance;
import com.fiberplus.main.entities.BoardEntity;
import com.fiberplus.main.entities.TaskCompletionEntity;
import com.fiberplus.main.entities.TaskEntity;
import com.fiberplus.main.entities.UserEntity;
import com.fiberplus.main.repositories.IBoardRepository;
import com.fiberplus.main.repositories.ITaskCompletionRepository;
import com.fiberplus.main.repositories.ITaskRepository;
import com.fiberplus.main.repositories.IUserRepository;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final ITaskRepository taskRepo;
    private final ITaskCompletionRepository completionRepo;
    private final IBoardRepository boardRepo;
    private final IUserRepository userRepo;

    public ReportService(ITaskRepository taskRepo, IBoardRepository boardRepo,
            ITaskCompletionRepository completionRepo, IUserRepository userRepo) {
        this.taskRepo = taskRepo;
        this.completionRepo = completionRepo;
        this.userRepo = userRepo;
        this.boardRepo = boardRepo;
    }

    public ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate, String reportType) {
        logger.info("📊 Generando reporte {} para {}-{}", reportType, startDate, endDate);

        List<TaskEntity> allTasks = taskRepo.findAll();

        List<TaskEntity> completedTasks = allTasks.stream()
                .filter(task -> "CERRADO".equals(task.getStatus()))
                .filter(task -> task.getClosedAt() != null
                        && !task.getClosedAt().isBefore(startDate)
                        && !task.getClosedAt().isAfter(endDate))
                .collect(Collectors.toList());

        Set<String> completedTaskIds = completedTasks.stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toSet());

        List<TaskCompletionEntity> completions = completionRepo.findByTaskIdIn(completedTaskIds);

        ReportSummary summary = generateSummary(completedTasks, completions, allTasks);
        List<ReportTaskDetail> taskDetails = generateTaskDetails(completedTasks, completions);

        List<AvailableUser> availableUsers = loadAvailableUsers();
        List<AvailableBoard> availableBoards = loadAvailableBoards();

        return ReportDto.builder()
                .reportId(UUID.randomUUID().toString())
                .reportType(reportType)
                .generatedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .summary(summary)
                .tasks(taskDetails)
                .availableUsers(availableUsers)
                .availableBoards(availableBoards)
                .build();
    }

    public ReportDto generateUserReport(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("📊 Generando reporte de usuario {} para {}-{}", userId, startDate, endDate);

        List<TaskEntity> allTasks = taskRepo.findAll().stream()
                .filter(t -> t.getAssignedTo() != null && t.getAssignedTo().contains(userId))
                .collect(Collectors.toList());

        List<TaskEntity> completedTasks = allTasks.stream()
                .filter(task -> "CERRADO".equals(task.getStatus()))
                .filter(task -> task.getClosedAt() != null
                        && !task.getClosedAt().isBefore(startDate)
                        && !task.getClosedAt().isAfter(endDate))
                .collect(Collectors.toList());

        Set<String> completedTaskIds = completedTasks.stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toSet());

        List<TaskCompletionEntity> completions = completionRepo.findByTaskIdIn(completedTaskIds);

        ReportSummary summary = generateSummary(completedTasks, completions, allTasks);
        List<ReportTaskDetail> taskDetails = generateTaskDetails(completedTasks, completions);

        List<AvailableUser> availableUsers = loadAvailableUsers();
        List<AvailableBoard> availableBoards = loadAvailableBoards();

        return ReportDto.builder()
                .reportId(UUID.randomUUID().toString())
                .reportType("BY_USER")
                .generatedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .summary(summary)
                .tasks(taskDetails)
                .availableUsers(availableUsers)
                .availableBoards(availableBoards)
                .build();
    }

    public ReportDto generateBoardReport(String boardId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("📊 Generando reporte de tablero {} para {}-{}", boardId, startDate, endDate);

        List<TaskEntity> boardTasks = taskRepo.findByBoardId(boardId);

        List<TaskEntity> completedTasks = boardTasks.stream()
                .filter(task -> "CERRADO".equals(task.getStatus()))
                .filter(task -> task.getClosedAt() != null
                        && !task.getClosedAt().isBefore(startDate)
                        && !task.getClosedAt().isAfter(endDate))
                .collect(Collectors.toList());

        Set<String> completedTaskIds = completedTasks.stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toSet());

        List<TaskCompletionEntity> completions = completionRepo.findByTaskIdIn(completedTaskIds);

        ReportSummary summary = generateSummary(completedTasks, completions, boardTasks);
        List<ReportTaskDetail> taskDetails = generateTaskDetails(completedTasks, completions);

        List<AvailableUser> availableUsers = loadAvailableUsers();
        List<AvailableBoard> availableBoards = loadAvailableBoards();

        return ReportDto.builder()
                .reportId(UUID.randomUUID().toString())
                .reportType("BY_BOARD")
                .generatedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .summary(summary)
                .tasks(taskDetails)
                .availableUsers(availableUsers)
                .availableBoards(availableBoards)
                .build();
    }

    public ReportDto generateDashboard() {
        logger.info("📊 Generando dashboard (últimos 30 días)");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        return generateReport(thirtyDaysAgo, now, "DASHBOARD");
    }

    private List<AvailableUser> loadAvailableUsers() {
        return userRepo.findAll().stream()
                .map(user -> AvailableUser.builder()
                        .userId(user.getId())
                        .userName(user.getName() + " " + user.getLastname())
                        .email(user.getEmail())
                        .position(user.getPosition())
                        .photo(user.getPhoto())
                        .build())
                .sorted(Comparator.comparing(AvailableUser::getUserName))
                .collect(Collectors.toList());
    }

    private List<AvailableBoard> loadAvailableBoards() {
        return boardRepo.findAll().stream()
                .filter(board -> "ACTIVE".equals(board.getStatus()))
                .map(board -> {
                    long taskCount = taskRepo.findByBoardId(board.getId()).size();

                    return AvailableBoard.builder()
                            .boardId(board.getId())
                            .boardName(board.getTitle())
                            .taskCount((int) taskCount)
                            .createdAt(board.getCreatedAt())
                            .build();
                })
                .sorted(Comparator.comparing(AvailableBoard::getBoardName))
                .collect(Collectors.toList());
    }

    private ReportSummary generateSummary(List<TaskEntity> completedTasks,
            List<TaskCompletionEntity> completions,
            List<TaskEntity> allTasks) {
        int totalEvidences = completions.stream()
                .mapToInt(c -> c.getImageUrls() != null ? c.getImageUrls().size() : 0)
                .sum();

        List<UserPerformance> userPerformance = calculateUserPerformance(completions, completedTasks);
        List<BoardStatistics> boardStats = calculateBoardStatistics(allTasks, completedTasks);

        return ReportSummary.builder()
                .totalTasks(allTasks.size())
                .completedTasks(completedTasks.size())
                .pendingTasks(allTasks.size() - completedTasks.size())
                .totalEvidences(totalEvidences)
                .userPerformance(userPerformance)
                .boardStatistics(boardStats)
                .build();
    }

    private List<UserPerformance> calculateUserPerformance(List<TaskCompletionEntity> completions,
            List<TaskEntity> completedTasks) {
        Map<String, List<TaskCompletionEntity>> completionsByUser = completions.stream()
                .collect(Collectors.groupingBy(TaskCompletionEntity::getCompletedBy));

        return completionsByUser.entrySet().stream()
                .map(entry -> {
                    String userId = entry.getKey();
                    List<TaskCompletionEntity> userCompletions = entry.getValue();

                    UserEntity user = userRepo.findById(userId).orElse(null);
                    String userName = user != null ? user.getName() + " " + user.getLastname() : "Usuario desconocido";

                    int evidences = userCompletions.stream()
                            .mapToInt(c -> c.getImageUrls() != null ? c.getImageUrls().size() : 0)
                            .sum();

                    double avgTime = calculateAverageCompletionTimeForUser(userCompletions, completedTasks);

                    return UserPerformance.builder()
                            .userId(userId)
                            .userName(userName)
                            .tasksCompleted(userCompletions.size()) 
                            .evidencesProvided(evidences)
                            .averageCompletionTime(avgTime)
                            .build();
                })
                .sorted(Comparator.comparingInt(UserPerformance::getTasksCompleted).reversed())
                .collect(Collectors.toList());
    }

    private double calculateAverageCompletionTimeForUser(List<TaskCompletionEntity> userCompletions,
            List<TaskEntity> tasks) {
        Map<String, TaskEntity> taskMap = tasks.stream()
                .collect(Collectors.toMap(TaskEntity::getId, t -> t));

        List<Long> durations = userCompletions.stream()
                .filter(c -> taskMap.containsKey(c.getTaskId()))
                .map(c -> {
                    TaskEntity task = taskMap.get(c.getTaskId());
                    return Duration.between(task.getCreatedAt(), c.getCompletedAt()).toHours();
                })
                .collect(Collectors.toList());

        return durations.isEmpty() ? 0
                : durations.stream()
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0);
    }

    private double calculateAverageCompletionTime(List<TaskCompletionEntity> completions,
            List<TaskEntity> tasks) {
        Map<String, TaskEntity> taskMap = tasks.stream()
                .collect(Collectors.toMap(TaskEntity::getId, t -> t));

        List<Long> durations = completions.stream()
                .filter(c -> taskMap.containsKey(c.getTaskId()))
                .map(c -> {
                    TaskEntity task = taskMap.get(c.getTaskId());
                    return Duration.between(task.getCreatedAt(), c.getCompletedAt()).toHours();
                })
                .collect(Collectors.toList());

        return durations.isEmpty() ? 0
                : durations.stream()
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0);
    }

    private List<BoardStatistics> calculateBoardStatistics(List<TaskEntity> allTasks,
            List<TaskEntity> completedTasks) {
        Map<String, List<TaskEntity>> tasksByBoard = allTasks.stream()
                .collect(Collectors.groupingBy(TaskEntity::getBoardId));

        Set<String> completedTaskIds = completedTasks.stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toSet());

        return tasksByBoard.entrySet().stream()
                .map(entry -> {
                    String boardId = entry.getKey();
                    List<TaskEntity> boardTasks = entry.getValue();

                    BoardEntity board = boardRepo.findById(boardId).orElse(null);
                    String boardName = board != null ? board.getTitle() : "Tablero desconocido";

                    long completed = boardTasks.stream()
                            .filter(task -> completedTaskIds.contains(task.getId()))
                            .count();

                    double completionRate = boardTasks.isEmpty() ? 0 : (double) completed / boardTasks.size() * 100;

                    return BoardStatistics.builder()
                            .boardId(boardId)
                            .boardName(boardName)
                            .totalTasks(boardTasks.size())
                            .completedTasks((int) completed)
                            .completionRate(completionRate)
                            .build();
                })
                .sorted(Comparator.comparingDouble(BoardStatistics::getCompletionRate).reversed())
                .collect(Collectors.toList());
    }

    private List<ReportTaskDetail> generateTaskDetails(List<TaskEntity> tasks,
            List<TaskCompletionEntity> completions) {
        Map<String, TaskCompletionEntity> completionMap = completions.stream()
                .collect(Collectors.toMap(TaskCompletionEntity::getTaskId, c -> c, (c1, c2) -> c1));

        return tasks.stream()
                .map(task -> {
                    TaskCompletionEntity completion = completionMap.get(task.getId());

                    BoardEntity board = boardRepo.findById(task.getBoardId()).orElse(null);
                    String boardName = board != null ? board.getTitle() : "Tablero desconocido";

                    List<String> assignedUserNames = new ArrayList<>();
                    if (task.getAssignedTo() != null) {
                        assignedUserNames = task.getAssignedTo().stream()
                                .map(userId -> {
                                    UserEntity user = userRepo.findById(userId).orElse(null);
                                    return user != null ? user.getName() + " " + user.getLastname() : userId;
                                })
                                .collect(Collectors.toList());
                    }

                    String completedByName = "";
                    String completedById = null;
                    if (completion != null) {
                        completedById = completion.getCompletedBy(); 
                        UserEntity user = userRepo.findById(completion.getCompletedBy()).orElse(null);
                        completedByName = user != null ? user.getName() + " " + user.getLastname()
                                : "Usuario desconocido";
                    }

                    return ReportTaskDetail.builder()
                            .taskId(task.getId())
                            .taskTitle(task.getTitle())
                            .boardName(boardName)
                            .priority(task.getPriority())
                            .status(task.getStatus() != null ? task.getStatus()
                                    : (completion != null ? "CERRADO" : "ABIERTO"))
                            .createdAt(task.getCreatedAt())
                            .completedAt(completion != null ? completion.getCompletedAt() : null)
                            .completedBy(completedById) 
                            .completedByName(completedByName) 
                            .completionDescription(completion != null ? completion.getDescription() : null)
                            .completionNotes(completion != null ? completion.getNotes() : null)
                            .evidenceUrls(completion != null ? completion.getImageUrls() : new ArrayList<>())
                            .assignedUsers(assignedUserNames) 
                            .build();
                })
                .sorted(Comparator.comparing(ReportTaskDetail::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}