package com.truman.punto_venta.venta.webui;

import com.truman.punto_venta.cliente.service.ClienteService;
import com.truman.punto_venta.producto.service.ProductoService;
import com.truman.punto_venta.venta.domain.MetodoPago;
import com.truman.punto_venta.venta.domain.Venta;
import com.truman.punto_venta.venta.service.VentaService;
import com.truman.punto_venta.venta.web.NuevaVentaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentaPageController {

    private final VentaService   ventaService;
    private final ProductoService productoService;
    private final ClienteService  clienteService;

    public VentaPageController(VentaService ventaService,
                                ProductoService productoService,
                                ClienteService clienteService) {
        this.ventaService    = ventaService;
        this.productoService = productoService;
        this.clienteService  = clienteService;
    }

    // ── Historial ────────────────────────────────────────────────

    @GetMapping
    public String historial(
            @RequestParam(defaultValue = "") String cliente,
            @PageableDefault(size = 15, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        Page<Venta> page = ventaService.listar(cliente.isBlank() ? null : cliente, pageable);
        model.addAttribute("page",    page);
        model.addAttribute("cliente", cliente);
        model.addAttribute("activeModule", "ventas");
        return "ventas/historial";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("venta",        ventaService.obtener(id));
        model.addAttribute("activeModule", "ventas");
        return "ventas/detalle";
    }

    // ── Caja (nueva venta) ───────────────────────────────────────

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        // Todos los productos activos para el buscador del carrito
        model.addAttribute("productos",    productoService.listar(null,
                Pageable.unpaged()).getContent());
        model.addAttribute("metodosPago",  MetodoPago.values());
        model.addAttribute("clientes",     clienteService.nombresActivos());
        model.addAttribute("activeModule", "ventas");
        return "ventas/caja";
    }

    /**
     * Recibe el carrito como JSON desde el formulario de caja (fetch POST).
     * Devuelve JSON con el id de la venta creada para redirigir al ticket.
     */
    @PostMapping("/nueva")
    @ResponseBody
    public java.util.Map<String, Object> crear(
            @RequestBody NuevaVentaRequest req) {

        Venta venta = ventaService.crear(req);
        return java.util.Map.of("ok", true, "ventaId", venta.getId());
    }

    // ── Ticket (vista de impresión) ──────────────────────────────

    @GetMapping("/{id}/ticket")
    public String ticket(@PathVariable Long id, Model model) {
        model.addAttribute("venta", ventaService.obtener(id));
        return "ventas/ticket";   // vista sin sidebar, lista para imprimir
    }
}