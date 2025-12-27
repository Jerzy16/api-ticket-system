package com.fiberplus.main.controllers.board;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.dtos.BoardDto;
import com.fiberplus.main.services.BoardService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("api/boards")
public class BoardController {

    private final BoardService _service;

    public BoardController(BoardService _service) {
        this._service = _service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BoardDto>> createBoard(@Valid @RequestBody BoardDto dto) {
        BoardDto board = _service.create(dto);
        return ResponseBuilder.created("Tablero creado exitosamente.", board);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardDto>>> getAll() {
        List<BoardDto> boards = _service.getAll();
        return ResponseBuilder.ok(
                "Lista de tableros obtenida exitosamente",
                boards);
    }



}
