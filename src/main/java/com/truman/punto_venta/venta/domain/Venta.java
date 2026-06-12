package com.truman.punto_venta.venta.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
public class Venta {

    @Getter @Setter
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    /** Nombre libre del cliente; null = venta sin cliente identificado */
    @Getter @Setter
    @Column(name = "cliente_nombre", length = 120)
    private String clienteNombre;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Getter @Setter
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Getter @Setter
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<VentaItem> items = new ArrayList<>();

    // ── helpers ─────────────────────────────────────────────────
    public void agregarItem(VentaItem item) {
        item.setVenta(this);
        items.add(item);
    }

    /** Recalcula el total sumando todos los subtotales. Llamar antes de persistir. */
    public void recalcularTotal() {
        this.total = items.stream()
                .map(VentaItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}