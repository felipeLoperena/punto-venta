package com.truman.punto_venta.reporte.web;

import java.math.BigDecimal;

/** Totales del periodo seleccionado. */
public record ReporteResumen(
        long       ventas,
        BigDecimal ingresos,
        BigDecimal ticketPromedio
) {}
