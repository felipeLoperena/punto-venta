package com.truman.punto_venta.cliente.repo;

import com.truman.punto_venta.cliente.domain.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /** Búsqueda por nombre o email (parcial, sin distinción de mayúsculas). */
    Page<Cliente> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nombre, String email, Pageable pageable);

    /** Nombres de clientes activos, para sugerir en la caja. */
    @Query("select c.nombre from Cliente c where c.activo = true order by c.nombre")
    List<String> findNombresActivos();
}
