package com.fiberplus.main.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.fiberplus.main.entities.BoardEntity;

@Repository
public interface IBoardRepository extends MongoRepository<BoardEntity, String>{
    Optional<BoardEntity> findByTitle(String title);
}
