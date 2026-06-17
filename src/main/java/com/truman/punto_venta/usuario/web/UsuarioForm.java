package com.truman.punto_venta.usuario.web;

import com.truman.punto_venta.usuario.domain.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuarioForm {

    // Solo se usa en edición
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Size(max = 120, message = "Máximo 120 caracteres")
    private String email;

    // Validación de longitud mínima se hace en el controlador según modo (crear/editar)
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    private boolean activo = true;
}