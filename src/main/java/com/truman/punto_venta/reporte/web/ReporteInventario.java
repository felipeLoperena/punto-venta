package com.truman.punto_venta.reporte.web;

import com.truman.punto_venta.producto.domain.Producto;

import java.math.BigDecimal;
import java.util.List;

/** Estado del inventario (independiente del periodo de fechas). */
public record ReporteInventario(
        BigDecimal     valor,
        long           productosActivos,
        long           stockBajo,
        List<Producto> productosStockBajo
) {}
