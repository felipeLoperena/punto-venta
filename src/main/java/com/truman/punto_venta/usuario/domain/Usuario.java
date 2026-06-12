package com.truman.punto_venta.usuario.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Getter @Setter
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    @Column(nullable = false, length = 100)
    private String nombre;

    @Getter @Setter
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Getter @Setter
    @Column(nullable = false, length = 255)
    private String password;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Getter @Setter
    @Column(nullable = false)
    private boolean activo = true;

    @Getter @Setter
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();
}