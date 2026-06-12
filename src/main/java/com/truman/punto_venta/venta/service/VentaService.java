package com.truman.punto_venta.venta.service;

import com.truman.punto_venta.producto.domain.Producto;
import com.truman.punto_venta.producto.repo.ProductoRepository;
import com.truman.punto_venta.venta.domain.Venta;
import com.truman.punto_venta.venta.domain.VentaItem;
import com.truman.punto_venta.venta.repo.VentaRepository;
import com.truman.punto_venta.venta.web.NuevaVentaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
public class VentaService {

    private final VentaRepository    ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository,
                        ProductoRepository productoRepository) {
        this.ventaRepository    = ventaRepository;
        this.productoRepository = productoRepository;
    }

    // ── Historial ────────────────────────────────────────────────

    public Page<Venta> listar(String clienteNombre, Pageable pageable) {
        if (clienteNombre != null && !clienteNombre.isBlank()) {
            return ventaRepository.findByClienteNombreContainingIgnoreCase(
                    clienteNombre.trim(), pageable);
        }
        return ventaRepository.findAll(pageable);
    }

    public Venta obtener(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Venta no encontrada: " + id));
    }

    // ── Crear venta ──────────────────────────────────────────────

    @Transactional
    public Venta crear(NuevaVentaRequest req) {

        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un producto.");
        }

        Venta venta = new Venta();
        venta.setClienteNombre(
                (req.getClienteNombre() != null && !req.getClienteNombre().isBlank())
                        ? req.getClienteNombre().trim()
                        : null);
        venta.setMetodoPago(req.getMetodoPago());

        for (NuevaVentaRequest.ItemRequest ir : req.getItems()) {

            // 1. Cargar producto y validar stock
            Producto producto = productoRepository.findById(ir.getProductoId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Producto no encontrado: " + ir.getProductoId()));

            if (!producto.isActivo()) {
                throw new IllegalStateException(
                        "El producto '" + producto.getNombre() + "' no está activo.");
            }
            if (producto.getStock() < ir.getCantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente para '" + producto.getNombre() + "'. "
                        + "Disponible: " + producto.getStock()
                        + ", solicitado: " + ir.getCantidad());
            }

            // 2. Construir ítem con snapshot de precios
            VentaItem item = new VentaItem();
            item.setProductoId(producto.getId());
            item.setProductoNombre(producto.getNombre());
            item.setPrecioUnitario(producto.getPrecio());
            item.setCantidad(ir.getCantidad());
            item.setDescuentoPct(
                    ir.getDescuentoPct() != null ? ir.getDescuentoPct() : BigDecimal.ZERO);
            item.calcularSubtotal();

            venta.agregarItem(item);

            // 3. Descontar stock
            producto.setStock(producto.getStock() - ir.getCantidad());
            productoRepository.save(producto);
        }

        // 4. Calcular total y persistir
        venta.recalcularTotal();
        return ventaRepository.save(venta);
    }
}