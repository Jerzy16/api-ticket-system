package com.fiberplus.main.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.dtos.AuthResponseDto;
import com.fiberplus.main.dtos.UserDto;
import com.fiberplus.main.dtos.auth.LoginDto;
import com.fiberplus.main.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService _auth;

    public AuthController(AuthService _auth) {
        this._auth = _auth;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(
            @Valid @RequestBody LoginDto dto
    ) {
        return ResponseBuilder.ok(
                "Login exitoso",
                _auth.login(dto.getEmail(), dto.getPassword())
        );
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> me() {
        return ResponseBuilder.ok(
                "Usuario autenticado",
                _auth.getCurrentUser()
        );
    }
}
