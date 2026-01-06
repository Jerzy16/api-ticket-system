package com.fiberplus.main.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fiberplus.main.dtos.UserDto;
import com.fiberplus.main.dtos.auth.AuthResponseDto;
import com.fiberplus.main.entities.UserEntity;
import com.fiberplus.main.exception.ResourceNotFoundException;
import com.fiberplus.main.repositories.IUserRepository;
import com.fiberplus.main.util.JwtUtil;

@Service
public class AuthService {

    private final IUserRepository _repo;
    private final PasswordEncoder _passwordEncoder;
    private final JwtUtil _jwtUtil;

    public AuthService(IUserRepository _repo, PasswordEncoder _passwordEncoder, JwtUtil _jwtUtil) {
        this._repo = _repo;
        this._passwordEncoder = _passwordEncoder;
        this._jwtUtil = _jwtUtil;
    }

    public AuthResponseDto login(String email, String password, boolean remerberMe) {

        UserEntity user = _repo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado", "UserService", "login"));

        if (!_passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = _jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                remerberMe);

        return new AuthResponseDto(token);
    }

    public UserDto getCurrentUser() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String userId = auth.getName();
        System.out.println("UserID extraído del token: " + userId);
        System.out.println("Tipo de Authentication: " + auth.getClass().getName());

        UserEntity user = _repo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado", "UserService", "getCurrentUser"));

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());

        return dto;
    }

}