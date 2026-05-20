package com.truman.punto_venta.producto.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.truman.punto_venta.producto.domain.Producto;
import com.truman.punto_venta.producto.repo.ProductoRepository;

@Service
public class ProductoService{

    @Autowired
    private ProductoRepository productoRepository;

    public Page<Producto> listar(String nombre, Pageable pageable) {
        if (nombre != null && !nombre.isBlank()) {
            return productoRepository.findByNombreContainingIgnoreCase(nombre.trim(), pageable);
        }
        return productoRepository.findAll(pageable);
    }

    public Producto obtener(Long id){
        return productoRepository.findById(id)
                                 .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: "+id));
    }

    public Producto crear(Producto p) {
        p.setId(null);
        return productoRepository.save(p);
    }

    public Producto actualizar(Long id, Producto p) {
        Producto actual = obtener(id);
        actual.setNombre(p.getNombre());
        actual.setPrecio(p.getPrecio());
        actual.setStock(p.getStock());
        actual.setCategoria(p.getCategoria());
        actual.setActivo(p.isActivo());
        return productoRepository.save(actual);
    }

    public void eliminar(Long id) {
        Producto actual = obtener(id);
        productoRepository.delete(actual);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by("nombre").ascending();
        // admite múltiples: sort=precio,desc&sort=nombre,asc (Spring lo parsea si vienes desde @RequestParam List<String> sort)
        String[] parts = sort.split(",", 2);
        String prop = parts[0].trim();
        String dir = (parts.length > 1 ? parts[1].trim().toLowerCase() : "asc");
        return "desc".equals(dir) ? Sort.by(prop).descending() : Sort.by(prop).ascending();
    }
}
