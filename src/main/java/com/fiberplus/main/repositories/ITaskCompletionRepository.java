package com.fiberplus.main.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.fiberplus.main.entities.TaskCompletionEntity;

@Repository
public interface ITaskCompletionRepository extends MongoRepository<TaskCompletionEntity, String> {
    List<TaskCompletionEntity> findByTaskId(String taskId);
    List<TaskCompletionEntity> findByBoardId(String boardId);
    List<TaskCompletionEntity> findByCompletedBy(String userId);
    List<TaskCompletionEntity> findByCompletedAtBetween(LocalDateTime start, LocalDateTime end);
    Optional<TaskCompletionEntity> findFirstByTaskIdOrderByCompletedAtDesc(String taskId);
}
