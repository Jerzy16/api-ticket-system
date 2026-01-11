package com.fiberplus.main.controllers.user;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.controllers.user.request.UpdateRequestUser;
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
            @Valid @RequestBody UpdateRequestUser dto) {
        UserDto updatedUser = UserDto.builder()
                .username(dto.getUsername())
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .position(dto.getPosition())
                .roles(dto.getRoles())
                .build();

        updatedUser = _service.update(id, updatedUser);
        return ResponseBuilder.ok("Usuario actualizado exitosamente", updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getById(@PathVariable String id) {
        UserDto user = _service.getById(id);
        return ResponseBuilder.ok("Usuario obtenido exitosamente", user);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAll() {
        List<UserDto> users = _service.getAll();
        return ResponseBuilder.ok("Lista de usuarios", users);
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

    @PostMapping("/{id}/photo")
    public ResponseEntity<ApiResponse<UserDto>> uploadProfilePhoto(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {

        UserDto updatedUser = _service.uploadProfilePhoto(id, file);
        return ResponseBuilder.ok("Foto de perfil actualizada exitosamente", updatedUser);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable String id,
            @RequestBody Map<String, String> passwordData) {

        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        _service.updatePassword(id, currentPassword, newPassword);
        return ResponseBuilder.ok("Contraseña actualizada exitosamente", null);
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<ApiResponse<UserDto>> deleteProfilePhoto(@PathVariable String id) {
        UserDto updatedUser = _service.deleteProfilePhoto(id);
        return ResponseBuilder.ok("Foto de perfil eliminada exitosamente", updatedUser);
    }
}