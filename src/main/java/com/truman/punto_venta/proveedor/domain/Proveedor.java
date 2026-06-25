package com.truman.punto_venta.proveedor.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @NotBlank
    @Size(min = 3, max = 120)
    @Column(nullable = false, length = 120)
    private String nombre;

    /** Persona de contacto dentro del proveedor. */
    @Getter
    @Setter
    @Size(max = 120)
    @Column(length = 120)
    private String contacto;

    @Getter
    @Setter
    @Email
    @Size(max = 120)
    @Column(length = 120)
    private String email;

    @Getter
    @Setter
    @Size(max = 20)
    @Column(length = 20)
    private String telefono;

    @Getter
    @Setter
    @Size(max = 160)
    @Column(length = 160)
    private String direccion;

    @Getter
    @Setter
    @Column(nullable = false)
    private boolean activo = true;

    @Getter
    @Setter
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();
}
