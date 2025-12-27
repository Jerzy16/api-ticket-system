package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fiberplus.main.dtos.BoardDto;
import com.fiberplus.main.entities.BoardEntity;
import com.fiberplus.main.exception.ConflictException;
import com.fiberplus.main.exception.ResourceNotFoundException;
import com.fiberplus.main.repositories.IBoardRepository;

@Service
public class BoardService {

    private final IBoardRepository _repo;

    public BoardService(IBoardRepository _repo) {
        this._repo = _repo;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public BoardDto create(BoardDto dto) {
        _repo.findByTitle(dto.getTitle())
                .ifPresent(e -> {
                    throw new ConflictException(
                            "El tablero con el título '" + dto.getTitle() + "' ya existe.");
                });

        String id = UUID.randomUUID().toString();
        String userId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        BoardEntity board = BoardEntity.builder()
                .id(id)
                .title(dto.getTitle())
                .createdBy(userId)
                .status("ACTIVE")
                .createdAt(now)
                .updatedAt(now)
                .build();

        board = _repo.save(board);

        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .createdBy(board.getCreatedBy())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

    private BoardDto toDto(BoardEntity board) {
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .createdBy(board.getCreatedBy())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .build();
    }

    public List<BoardDto> getAll() {
        return _repo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public BoardDto update(String id, BoardDto dto) {

        BoardEntity board = _repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el tablero con id ", "id", id));

        String userId = getCurrentUserId();

        _repo.findByTitle(dto.getTitle())
                .filter(b -> !b.getId().equals(id))
                .ifPresent(b -> {
                    throw new ConflictException(
                            "Ya existe un tablero con el título " + dto.getTitle());
                });

        BoardEntity updated = BoardEntity.builder()
                .id(board.getId())
                .title(dto.getTitle())
                .createdBy(userId)
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        updated = _repo.save(updated);

        return toDto(updated);
    }

    public void delete(String id) {

        BoardEntity board = _repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el tablero con id ", "id", id));

        board.setStatus("INACTIVE");
        board.setUpdatedAt(LocalDateTime.now());

        _repo.save(board);
    }

}
