package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fiberplus.main.entities.PasswordResetEntity;
import com.fiberplus.main.entities.UserEntity;
import com.fiberplus.main.exception.GenericException;
import com.fiberplus.main.repositories.IPasswordResetRepository;
import com.fiberplus.main.repositories.IUserRepository;

@Service
public class PasswordResetService {

    private final IPasswordResetRepository _repo;
    private final PasswordEncoder _passwordEncoder;
    private final IUserRepository _userRepository;
    private final EmailService emailService;

    public PasswordResetService(IPasswordResetRepository _repo, PasswordEncoder _passwordEncoder,
            IUserRepository _userRepository, EmailService emailService) {
        this._repo = _repo;
        this._passwordEncoder = _passwordEncoder;
        this._userRepository = _userRepository;
        this.emailService = emailService;
    }

    public void sendCode(String email) {

        _userRepository.findByEmail(email)
                .orElseThrow(() -> new GenericException("Este correo no existe"));

        String code = String.format("%06d", new Random().nextInt(9999999));
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        PasswordResetEntity passwordReset = PasswordResetEntity.builder()
                .id(id)
                .code(code)
                .used(false)
                .createdAt(now)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .updatedAt(now)
                .build();

        _repo.save(passwordReset);
        emailService.sendPasswordResetCode(email, code);
    }

    public void verifyCode(String email, String code) {

        PasswordResetEntity passwordReset = _repo.findByEmailAndCodeAndUsedFalse(email, code)
                .orElseThrow(() -> new GenericException("Código inválido"));

        if (passwordReset.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GenericException("Código expirado");
        }
    }

    public void resetPassword(String email, String code, String newPassword) {
        PasswordResetEntity passwordReset = _repo.findByEmailAndCodeAndUsedFalse(email, code)
                .orElseThrow(() -> new GenericException("Código inválido"));
        if (passwordReset.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GenericException("Código expirado");
        }

        UserEntity user = _userRepository.findByEmail(email)
                .orElseThrow(() -> new GenericException("Usuaior no encontrado"));

        user.setPassword(_passwordEncoder.encode(newPassword));
        _userRepository.save(user);

        passwordReset.setUsed(true);
        _repo.save(passwordReset);
    }

}
