package com.fiberplus.main.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.fiberplus.main.entities.TaskEntity;

@Repository
public interface ITaskRepository extends MongoRepository<TaskEntity, String> {
    List<TaskEntity> findByBoardId(String id);
}
