package com.truman.punto_venta.reporte.web;

import java.math.BigDecimal;

/** Producto vendido en el periodo: unidades e ingresos. */
public record FilaProducto(
        String     nombre,
        long       unidades,
        BigDecimal ingresos
) {}
