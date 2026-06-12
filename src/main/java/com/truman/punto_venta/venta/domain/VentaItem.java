package com.truman.punto_venta.venta.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "venta_item")
public class VentaItem {

    @Getter @Setter
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    /** FK al producto — se guarda para referencia, pero los datos clave son snapshots */
    @Getter @Setter
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    /** Snapshot del nombre al momento de vender */
    @Getter @Setter
    @Column(name = "producto_nombre", nullable = false, length = 80)
    private String productoNombre;

    /** Snapshot del precio al momento de vender */
    @Getter @Setter
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    /** Descuento en porcentaje: 0–100 */
    @Getter @Setter
    @Column(name = "descuento_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoPct = BigDecimal.ZERO;

    @Getter @Setter
    @Column(nullable = false)
    private Integer cantidad;

    @Getter @Setter
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    // ── helper ───────────────────────────────────────────────────
    /**
     * Calcula y asigna el subtotal:
     *   subtotal = cantidad × precioUnitario × (1 − descuentoPct/100)
     */
    public void calcularSubtotal() {
        BigDecimal factor = BigDecimal.ONE
                .subtract(descuentoPct.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
        this.subtotal = precioUnitario
                .multiply(new BigDecimal(cantidad))
                .multiply(factor)
                .setScale(2, RoundingMode.HALF_UP);
    }
}