package com.truman.punto_venta.venta.web;

import com.truman.punto_venta.venta.domain.MetodoPago;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO que llega del formulario de caja (JSON via fetch).
 * Contiene los datos de la venta y la lista de ítems del carrito.
 */
@Getter @Setter
public class NuevaVentaRequest {

    private String clienteNombre;       // nullable
    private MetodoPago metodoPago;      // EFECTIVO | TARJETA

    private List<ItemRequest> items;

    @Getter @Setter
    public static class ItemRequest {
        private Long   productoId;
        private int    cantidad;
        private BigDecimal descuentoPct; // 0–100, default 0
    }
}