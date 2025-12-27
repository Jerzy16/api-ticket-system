package com.fiberplus.main.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDto {
    @Email(message = "El correo no tiene el formato requerido")
    @NotBlank(message = "El correo es un campo obligatorio")
    String email;

    @NotBlank(message = "La contraseña es un campo obligatorio")
    String password;

    boolean rememberMe;
}
