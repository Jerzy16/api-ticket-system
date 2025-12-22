package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fiberplus.main.dtos.UserDto;
import com.fiberplus.main.entities.UserEntity;
import com.fiberplus.main.repositories.IUserRepository;

@Service
public class UserService {
    
    private final IUserRepository _repo;

    public UserService(IUserRepository _repo){
        this._repo = _repo;
    }

    public void insert(UserDto dto) {

        String id = UUID.randomUUID().toString();
        LocalDateTime date = LocalDateTime.now();

        try {
            UserEntity user = new UserEntity();
            user.setId(id);
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());
            user.setCreatedAt(date);
            user.setUpdatedAt(date);
            _repo.save(user);
        } catch (Exception e) {
        }

    }

}
