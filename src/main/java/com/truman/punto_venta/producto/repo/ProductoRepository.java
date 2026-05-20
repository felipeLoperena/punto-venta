package com.truman.punto_venta.producto.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.truman.punto_venta.producto.domain.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{
    // Filtro por nombre (containing, case-insensitive si usas LOWER en el spec)
  Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
