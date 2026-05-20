package com.truman.punto_venta.producto.domain;
    
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
    
@Entity
@Table(name = "producto")
public class Producto {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Getter
    @Setter
    @NotBlank
    @Size(min = 3, max = 80)
    @Column(nullable = false, length = 80)
    private String nombre;
    
    @Getter
    @Setter
    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;
    
    @Getter
    @Setter
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer stock;

    @Getter
    @Setter
    @NotBlank
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String categoria;
    
    @Getter
    @Setter
    @Column(nullable = false)
    private boolean activo = true;    
}
