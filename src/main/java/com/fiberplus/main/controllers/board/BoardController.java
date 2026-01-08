package com.fiberplus.main.controllers.board;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.dtos.BoardDto;
import com.fiberplus.main.dtos.BoardWithTasksDto;
import com.fiberplus.main.services.BoardService;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("api/boards")
@Tag(name = "Boards", description = "API para gestión de tableros")
public class BoardController {

    private final BoardService _service;

    public BoardController(BoardService _service) {
        this._service = _service;
    }

    @PostMapping
    @Operation(
        summary = "Crear un nuevo tablero",
        description = "Crea un tablero con el título especificado"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Tablero creado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "El tablero ya existe"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
        )
    })
    public ResponseEntity<ApiResponse<BoardDto>> createBoard(
            @Valid @RequestBody 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del tablero a crear",
                required = true
            ) 
            BoardDto dto) {
        BoardDto board = _service.create(dto);
        return ResponseBuilder.created("Tablero creado exitosamente.", board);
    }

    @GetMapping
    @Operation(
        summary = "Obtener todos los tableros",
        description = "Retorna una lista de todos los tableros activos sin tareas"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Lista obtenida exitosamente"
    )
    public ResponseEntity<ApiResponse<List<BoardDto>>> getAll() {
        List<BoardDto> boards = _service.getAll();
        return ResponseBuilder.ok("Lista de tableros obtenida exitosamente", boards);
    }
    
    @GetMapping("/with-tasks")
    @Operation(
        summary = "Obtener tableros con tareas",
        description = "Retorna todos los tableros con sus tareas asociadas"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Tableros con tareas obtenidos exitosamente"
    )
    public ResponseEntity<ApiResponse<List<BoardWithTasksDto>>> getAllBoardsWithTasks() {
        List<BoardWithTasksDto> boards = _service.getAllWithTasks();
        return ResponseBuilder.ok("Tableros con tareas obtenidos exitosamente", boards);
    }

    @GetMapping("/{id}/with-tasks")
    @Operation(
        summary = "Obtener un tablero específico con tareas",
        description = "Retorna un tablero con todas sus tareas"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Tablero encontrado"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Tablero no encontrado"
        )
    })
    public ResponseEntity<ApiResponse<BoardWithTasksDto>> getBoardWithTasks(
            @Parameter(description = "ID del tablero", required = true)
            @PathVariable String id) {
        BoardWithTasksDto board = _service.getByIdWithTasks(id);
        return ResponseBuilder.ok("Tablero con tareas obtenido exitosamente", board);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar un tablero",
        description = "Actualiza el título de un tablero existente"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tablero actualizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tablero no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Título ya existe")
    })
    public ResponseEntity<ApiResponse<BoardDto>> updateBoard(
            @Parameter(description = "ID del tablero", required = true)
            @PathVariable String id,
            @Valid @RequestBody BoardDto dto) {
        BoardDto updatedBoard = _service.update(id, dto);
        return ResponseBuilder.ok("Tablero actualizado exitosamente", updatedBoard);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar un tablero",
        description = "Realiza un soft delete del tablero (cambia estado a INACTIVE)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tablero eliminado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tablero no encontrado")
    })
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @Parameter(description = "ID del tablero a eliminar", required = true)
            @PathVariable String id) {
        _service.delete(id);
        return ResponseBuilder.deleted("Tablero eliminado exitosamente");
    }
}