package com.fiberplus.main.controllers.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiberplus.main.dtos.UserDto;
import com.fiberplus.main.response.ApiResponse;
import com.fiberplus.main.services.UserService;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService _service;

    public UserController(UserService _service) {
        this._service = _service;
    }

    @PostMapping
    public ApiResponse<UserDto> create(@RequestBody UserDto dto) {
        _service.insert(dto);
        return ApiResponse.success("Usuario creado correctamente", dto);
    }

}
