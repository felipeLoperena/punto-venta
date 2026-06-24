package com.truman.punto_venta.cliente.service;

import com.truman.punto_venta.cliente.domain.Cliente;
import com.truman.punto_venta.cliente.repo.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Page<Cliente> listar(String q, Pageable pageable) {
        if (q != null && !q.isBlank()) {
            String termino = q.trim();
            return clienteRepository
                    .findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(termino, termino, pageable);
        }
        return clienteRepository.findAll(pageable);
    }

    /** Nombres de clientes activos para autocompletar en la caja. */
    public java.util.List<String> nombresActivos() {
        return clienteRepository.findNombresActivos();
    }

    public Cliente obtener(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado: " + id));
    }

    public Cliente crear(Cliente c) {
        c.setId(null);
        normalizar(c);
        return clienteRepository.save(c);
    }

    public Cliente actualizar(Long id, Cliente c) {
        Cliente actual = obtener(id);
        actual.setNombre(c.getNombre());
        actual.setEmail(c.getEmail());
        actual.setTelefono(c.getTelefono());
        actual.setDireccion(c.getDireccion());
        actual.setActivo(c.isActivo());
        normalizar(actual);
        return clienteRepository.save(actual);
    }

    public void eliminar(Long id) {
        clienteRepository.delete(obtener(id));
    }

    /** Convierte los campos opcionales en blanco a null para no guardar cadenas vacías. */
    private void normalizar(Cliente c) {
        c.setEmail(vacioANull(c.getEmail()));
        c.setTelefono(vacioANull(c.getTelefono()));
        c.setDireccion(vacioANull(c.getDireccion()));
    }

    private String vacioANull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
