package com.fiberplus.main.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.fiberplus.main.entities.UserEntity;

@Repository
public interface IUserRepository extends MongoRepository<UserEntity, String> {
     Optional<UserEntity> findByEmail(String email);
    
    Optional<UserEntity> findByUsername(String username);
    
    boolean existsByRolesContaining(String role);
    
    long countByRolesContaining(String role);

}
