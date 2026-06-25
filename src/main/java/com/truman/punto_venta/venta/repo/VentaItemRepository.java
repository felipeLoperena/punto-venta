package com.truman.punto_venta.venta.repo;

import com.truman.punto_venta.venta.domain.VentaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaItemRepository extends JpaRepository<VentaItem, Long> {

    /**
     * Productos más vendidos en el rango [desde, hasta), agrupados por el nombre
     * snapshot. Cada fila: [productoNombre, unidades, ingresos]. Ordenado por
     * unidades vendidas (desc).
     */
    @Query("""
            select vi.productoNombre, coalesce(sum(vi.cantidad), 0), coalesce(sum(vi.subtotal), 0)
            from VentaItem vi
            where vi.venta.fecha >= :desde and vi.venta.fecha < :hasta
            group by vi.productoNombre
            order by sum(vi.cantidad) desc
            """)
    List<Object[]> masVendidos(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
