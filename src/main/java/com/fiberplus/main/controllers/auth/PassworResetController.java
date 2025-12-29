package com.fiberplus.main.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.services.PasswordResetService;

@RestController
@RequestMapping("/api/auth")
public class PassworResetController {

    private final PasswordResetService _service;

    public PassworResetController(PasswordResetService _service) {
        this._service = _service;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        _service.sendCode(email);
        return ResponseBuilder.ok("Código enviado al correo", null);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestParam String email, @RequestParam String code) {
        _service.verifyCode(email, code);
        return ResponseBuilder.ok("Código validado", null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestParam String email, @RequestParam String code,
            @RequestParam String newPassword) {
        _service.resetPassword(email, code, newPassword);
        return ResponseBuilder.ok("Contraseña actualizada", null);
    }

}
