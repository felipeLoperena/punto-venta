package com.truman.punto_venta.venta.repo;

import com.truman.punto_venta.venta.domain.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    /** Búsqueda por nombre de cliente (parcial, sin distinción de mayúsculas) */
    Page<Venta> findByClienteNombreContainingIgnoreCase(String clienteNombre, Pageable pageable);

    /** Suma de ingresos desde una fecha (inclusive); 0 si no hay ventas. */
    @Query("select coalesce(sum(v.total), 0) from Venta v where v.fecha >= :desde")
    BigDecimal sumTotalDesde(@Param("desde") LocalDateTime desde);

    /** Cantidad de ventas desde una fecha (inclusive). */
    long countByFechaGreaterThanEqual(LocalDateTime desde);

    /** Últimas ventas para el panel de actividad reciente. */
    List<Venta> findTop5ByOrderByFechaDesc();

    /** Ingresos agrupados por día de un mes concreto. Cada fila: [dia (int), total]. */
    @Query("""
            select day(v.fecha), coalesce(sum(v.total), 0)
            from Venta v
            where year(v.fecha) = :anio and month(v.fecha) = :mes
            group by day(v.fecha)
            order by day(v.fecha)
            """)
    List<Object[]> ingresosPorDia(@Param("anio") int anio, @Param("mes") int mes);

    /** Ingresos agrupados por mes desde una fecha. Cada fila: [anio, mes, total]. */
    @Query("""
            select year(v.fecha), month(v.fecha), coalesce(sum(v.total), 0)
            from Venta v
            where v.fecha >= :desde
            group by year(v.fecha), month(v.fecha)
            order by year(v.fecha), month(v.fecha)
            """)
    List<Object[]> ingresosPorMes(@Param("desde") LocalDateTime desde);

    // ── Reportes (rango [desde, hasta) ) ─────────────────────────

    /** Ingresos totales en el rango; 0 si no hay ventas. */
    @Query("select coalesce(sum(v.total), 0) from Venta v where v.fecha >= :desde and v.fecha < :hasta")
    BigDecimal sumTotalEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    /** Cantidad de ventas en el rango. */
    long countByFechaGreaterThanEqualAndFechaLessThan(LocalDateTime desde, LocalDateTime hasta);

    /** Ventas e ingresos por día. Cada fila: [anio, mes, dia, nVentas, total]. */
    @Query("""
            select year(v.fecha), month(v.fecha), day(v.fecha), count(v), coalesce(sum(v.total), 0)
            from Venta v
            where v.fecha >= :desde and v.fecha < :hasta
            group by year(v.fecha), month(v.fecha), day(v.fecha)
            order by year(v.fecha), month(v.fecha), day(v.fecha)
            """)
    List<Object[]> ventasPorDia(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    /** Ventas e ingresos por método de pago. Cada fila: [metodoPago, nVentas, total]. */
    @Query("""
            select v.metodoPago, count(v), coalesce(sum(v.total), 0)
            from Venta v
            where v.fecha >= :desde and v.fecha < :hasta
            group by v.metodoPago
            order by sum(v.total) desc
            """)
    List<Object[]> ventasPorMetodo(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    /** Ventas e ingresos por cliente (texto libre; null = sin cliente). Cada fila: [clienteNombre, nVentas, total]. */
    @Query("""
            select v.clienteNombre, count(v), coalesce(sum(v.total), 0)
            from Venta v
            where v.fecha >= :desde and v.fecha < :hasta
            group by v.clienteNombre
            order by sum(v.total) desc
            """)
    List<Object[]> ventasPorCliente(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}