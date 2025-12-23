package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fiberplus.main.dtos.UserDto;
import com.fiberplus.main.entities.UserEntity;
import com.fiberplus.main.exception.ConflictException;
import com.fiberplus.main.repositories.IUserRepository;

@Service
public class UserService {

    private final IUserRepository _repo;
    private final PasswordEncoder _passwordEncoder;

    public UserService(IUserRepository _repo, PasswordEncoder _passwordEncoder) {
        this._repo = _repo;
        this._passwordEncoder = _passwordEncoder;
    }

    public void insert(UserDto dto) {

        _repo.findByEmail(dto.getEmail())
                .ifPresent(e -> {
                    throw new ConflictException("El proveedor con el correo: '" + dto.getEmail() + "' ya existe.");
                });

        String id = UUID.randomUUID().toString();
        LocalDateTime date = LocalDateTime.now();
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(_passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(date);
        user.setUpdatedAt(date);
        _repo.save(user);
    }

}
