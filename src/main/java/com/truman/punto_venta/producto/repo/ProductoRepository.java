package com.truman.punto_venta.producto.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.truman.punto_venta.producto.domain.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{
    // Filtro por nombre (containing, case-insensitive si usas LOWER en el spec)
  Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

  /** Cantidad de productos activos (catálogo vigente). */
  long countByActivoTrue();

  /** Cantidad de productos activos con stock en o por debajo del umbral. */
  long countByActivoTrueAndStockLessThanEqual(int stock);

  /** Productos activos con stock bajo, los más críticos primero. */
  List<Producto> findTop5ByActivoTrueAndStockLessThanEqualOrderByStockAsc(int stock);
}
