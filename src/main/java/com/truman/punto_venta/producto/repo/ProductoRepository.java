package com.truman.punto_venta.producto.repo;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.truman.punto_venta.producto.domain.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{
    // Filtro por nombre (containing, case-insensitive si usas LOWER en el spec)
  Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

  /** Categorías distintas existentes, para sugerir en el formulario. */
  @Query("select distinct p.categoria from Producto p order by p.categoria")
  List<String> findCategorias();

  /** Cantidad de productos activos (catálogo vigente). */
  long countByActivoTrue();

  /** Cantidad de productos activos con stock en o por debajo del umbral. */
  long countByActivoTrueAndStockLessThanEqual(int stock);

  /** Productos activos con stock bajo, los más críticos primero. */
  List<Producto> findTop5ByActivoTrueAndStockLessThanEqualOrderByStockAsc(int stock);

  /** Lista completa de productos activos con stock bajo, los más críticos primero. */
  List<Producto> findByActivoTrueAndStockLessThanEqualOrderByStockAsc(int stock);

  /** Valor del inventario activo a precio de venta: suma de precio × stock. */
  @Query("select coalesce(sum(p.precio * p.stock), 0) from Producto p where p.activo = true")
  BigDecimal valorInventario();
}
