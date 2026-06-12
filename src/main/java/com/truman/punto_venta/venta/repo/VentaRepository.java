package com.truman.punto_venta.venta.repo;

import com.truman.punto_venta.venta.domain.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    /** Búsqueda por nombre de cliente (parcial, sin distinción de mayúsculas) */
    Page<Venta> findByClienteNombreContainingIgnoreCase(String clienteNombre, Pageable pageable);
}