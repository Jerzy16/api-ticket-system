package com.fiberplus.main.controllers.user.request;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UpdateRequestUser {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 30, message = "El nombre de usuario debe tener entre 4 y 30 caracteres")
    private String username;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El apellido de usuario es obligatorio")
    private String lastName;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido")
    @Size(max = 100, message = "El correo electrónico no puede exceder 100 caracteres")
    private String email;

    @NotNull(message = "El cargo es obligatorio")
    @NotBlank(message = "El cargo no puede estar vacío")
    @Size(max = 50, message = "El cargo no puede exceder 50 caracteres")
    private String position;

    @Size(max = 255, message = "La URL de la foto es demasiado larga")
    private String photo;

    @NotNull(message = "El rol es obligatorio")
    @NotEmpty(message = "Debe especificar al menos un rol")
    private Set<String> roles = new HashSet<>();

}
