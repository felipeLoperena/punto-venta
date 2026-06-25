package com.truman.punto_venta.proveedor.repo;

import com.truman.punto_venta.proveedor.domain.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    /** Búsqueda por nombre o contacto (parcial, sin distinción de mayúsculas). */
    Page<Proveedor> findByNombreContainingIgnoreCaseOrContactoContainingIgnoreCase(
            String nombre, String contacto, Pageable pageable);
}
