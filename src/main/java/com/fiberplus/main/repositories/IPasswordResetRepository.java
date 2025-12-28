package com.fiberplus.main.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.fiberplus.main.entities.PasswordResetEntity;

@Repository
public interface IPasswordResetRepository extends MongoRepository<PasswordResetEntity, String> {
    Optional<PasswordResetEntity> findByEmailAndCodeAndUsedFalse(String email, String code);
}
