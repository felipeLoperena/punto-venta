package com.truman.punto_venta.dashboard.web;

import com.truman.punto_venta.producto.domain.Producto;
import com.truman.punto_venta.venta.domain.Venta;

import java.math.BigDecimal;
import java.util.List;

/**
 * Datos agregados que alimentan la vista del dashboard. Es de solo lectura:
 * lo construye {@code DashboardService} a partir de los repositorios.
 */
public record DashboardResumen(
        long       ventasHoy,
        BigDecimal ingresosHoy,
        BigDecimal ingresosMes,
        long       productosActivos,
        long       stockBajo,
        List<Venta>    ultimasVentas,
        List<Producto> productosStockBajo
) {}
