package com.truman.punto_venta.proveedor.service;

import com.truman.punto_venta.proveedor.domain.Proveedor;
import com.truman.punto_venta.proveedor.repo.ProveedorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public Page<Proveedor> listar(String q, Pageable pageable) {
        if (q != null && !q.isBlank()) {
            String termino = q.trim();
            return proveedorRepository
                    .findByNombreContainingIgnoreCaseOrContactoContainingIgnoreCase(termino, termino, pageable);
        }
        return proveedorRepository.findAll(pageable);
    }

    public Proveedor obtener(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado: " + id));
    }

    public Proveedor crear(Proveedor p) {
        p.setId(null);
        normalizar(p);
        return proveedorRepository.save(p);
    }

    public Proveedor actualizar(Long id, Proveedor p) {
        Proveedor actual = obtener(id);
        actual.setNombre(p.getNombre());
        actual.setContacto(p.getContacto());
        actual.setEmail(p.getEmail());
        actual.setTelefono(p.getTelefono());
        actual.setDireccion(p.getDireccion());
        actual.setActivo(p.isActivo());
        normalizar(actual);
        return proveedorRepository.save(actual);
    }

    public void eliminar(Long id) {
        proveedorRepository.delete(obtener(id));
    }

    /** Convierte los campos opcionales en blanco a null para no guardar cadenas vacías. */
    private void normalizar(Proveedor p) {
        p.setContacto(vacioANull(p.getContacto()));
        p.setEmail(vacioANull(p.getEmail()));
        p.setTelefono(vacioANull(p.getTelefono()));
        p.setDireccion(vacioANull(p.getDireccion()));
    }

    private String vacioANull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
