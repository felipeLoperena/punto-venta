package com.truman.punto_venta.reporte.service;

import com.truman.punto_venta.producto.domain.Producto;
import com.truman.punto_venta.producto.repo.ProductoRepository;
import com.truman.punto_venta.reporte.web.*;
import com.truman.punto_venta.venta.domain.MetodoPago;
import com.truman.punto_venta.venta.repo.VentaItemRepository;
import com.truman.punto_venta.venta.repo.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteService {

    /** Umbral por debajo del cual un producto se considera con stock bajo. */
    public static final int UMBRAL_STOCK_BAJO = 5;

    private final VentaRepository     ventaRepository;
    private final VentaItemRepository ventaItemRepository;
    private final ProductoRepository  productoRepository;

    public ReporteService(VentaRepository ventaRepository,
                          VentaItemRepository ventaItemRepository,
                          ProductoRepository productoRepository) {
        this.ventaRepository     = ventaRepository;
        this.ventaItemRepository = ventaItemRepository;
        this.productoRepository  = productoRepository;
    }

    // ── Reportes por periodo ─────────────────────────────────────

    @Transactional(readOnly = true)
    public ReporteResumen resumen(LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();
        long ventas = ventaRepository.countByFechaGreaterThanEqualAndFechaLessThan(ini, fin);
        BigDecimal ingresos = ventaRepository.sumTotalEntre(ini, fin);
        BigDecimal ticket = ventas > 0
                ? ingresos.divide(BigDecimal.valueOf(ventas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        return new ReporteResumen(ventas, ingresos, ticket);
    }

    @Transactional(readOnly = true)
    public List<FilaDia> ventasPorDia(LocalDate desde, LocalDate hasta) {
        List<FilaDia> filas = new ArrayList<>();
        for (Object[] f : ventaRepository.ventasPorDia(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay())) {
            LocalDate fecha = LocalDate.of(num(f[0]), num(f[1]), num(f[2]));
            filas.add(new FilaDia(fecha, ((Number) f[3]).longValue(), (BigDecimal) f[4]));
        }
        return filas;
    }

    @Transactional(readOnly = true)
    public List<FilaProducto> productosMasVendidos(LocalDate desde, LocalDate hasta) {
        List<FilaProducto> filas = new ArrayList<>();
        for (Object[] f : ventaItemRepository.masVendidos(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay())) {
            filas.add(new FilaProducto((String) f[0], ((Number) f[1]).longValue(), (BigDecimal) f[2]));
        }
        return filas;
    }

    @Transactional(readOnly = true)
    public List<FilaCategoria> ventasPorMetodo(LocalDate desde, LocalDate hasta) {
        List<FilaCategoria> filas = new ArrayList<>();
        for (Object[] f : ventaRepository.ventasPorMetodo(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay())) {
            String etiqueta = ((MetodoPago) f[0]).getLabel();
            filas.add(new FilaCategoria(etiqueta, ((Number) f[1]).longValue(), (BigDecimal) f[2]));
        }
        return filas;
    }

    @Transactional(readOnly = true)
    public List<FilaCategoria> ventasPorCliente(LocalDate desde, LocalDate hasta) {
        List<FilaCategoria> filas = new ArrayList<>();
        for (Object[] f : ventaRepository.ventasPorCliente(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay())) {
            String cliente = f[0] != null ? (String) f[0] : "Sin cliente";
            filas.add(new FilaCategoria(cliente, ((Number) f[1]).longValue(), (BigDecimal) f[2]));
        }
        return filas;
    }

    // ── Inventario (sin periodo) ─────────────────────────────────

    @Transactional(readOnly = true)
    public ReporteInventario inventario() {
        List<Producto> stockBajo =
                productoRepository.findByActivoTrueAndStockLessThanEqualOrderByStockAsc(UMBRAL_STOCK_BAJO);
        return new ReporteInventario(
                productoRepository.valorInventario(),
                productoRepository.countByActivoTrue(),
                stockBajo.size(),
                stockBajo);
    }

    private static int num(Object o) {
        return ((Number) o).intValue();
    }
}
