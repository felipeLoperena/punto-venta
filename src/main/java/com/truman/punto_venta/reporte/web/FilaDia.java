package com.truman.punto_venta.reporte.web;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Ventas e ingresos de un día. */
public record FilaDia(
        LocalDate  fecha,
        long       ventas,
        BigDecimal ingresos
) {}
