package com.fiberplus.main.controllers.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.dtos.UserDto;
import com.fiberplus.main.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService _service;

    public UserController(UserService _service) {
        this._service = _service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> create(@Valid @RequestBody UserDto dto) {
        _service.insert(dto);
        return ResponseBuilder.created("Usuario creado exitosamente", dto);
    }

}
