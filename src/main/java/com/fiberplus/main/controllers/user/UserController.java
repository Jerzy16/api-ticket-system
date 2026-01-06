package com.fiberplus.main.controllers.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.dtos.TeamGroupDto;
import com.fiberplus.main.dtos.UserDto;
import com.fiberplus.main.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService _service;

    public UserController(UserService _service) {
        this._service = _service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> create(@Valid @RequestBody UserDto dto) {
        UserDto createdUser = _service.insert(dto);
        return ResponseBuilder.created("Usuario creado exitosamente", createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> update(
            @PathVariable String id,
            @Valid @RequestBody UserDto dto) {
        UserDto updatedUser = _service.update(id, dto);
        return ResponseBuilder.ok("Usuario actualizado exitosamente", updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        _service.delete(id);
        return ResponseBuilder.ok("Usuario eliminado exitosamente", null);
    }

    @GetMapping("/team")
    public ResponseEntity<ApiResponse<List<TeamGroupDto>>> getTeam() {
        List<TeamGroupDto> team = _service.getTeam();
        return ResponseBuilder.ok("Lista del equipo", team);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getById(@PathVariable String id) {
        return ResponseBuilder.ok("Usuario obtenido", null);
    }
}