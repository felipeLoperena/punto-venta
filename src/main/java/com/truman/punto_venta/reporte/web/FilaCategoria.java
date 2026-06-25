package com.truman.punto_venta.reporte.web;

import java.math.BigDecimal;

/**
 * Fila genérica etiqueta/ventas/ingresos, reutilizada por los reportes de
 * método de pago y de cliente.
 */
public record FilaCategoria(
        String     etiqueta,
        long       ventas,
        BigDecimal ingresos
) {}
