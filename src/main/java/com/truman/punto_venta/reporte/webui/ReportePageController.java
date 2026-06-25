package com.truman.punto_venta.reporte.webui;

import com.truman.punto_venta.producto.domain.Producto;
import com.truman.punto_venta.reporte.service.ReporteService;
import com.truman.punto_venta.reporte.web.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ReportePageController {

    private final ReporteService service;

    public ReportePageController(ReporteService service) {
        this.service = service;
    }

    // ── Página ───────────────────────────────────────────────────

    @GetMapping("/reportes")
    public String reportes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        LocalDate[] rango = rango(desde, hasta);
        desde = rango[0];
        hasta = rango[1];

        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("resumen",   service.resumen(desde, hasta));
        model.addAttribute("porDia",     service.ventasPorDia(desde, hasta));
        model.addAttribute("productos",  service.productosMasVendidos(desde, hasta));
        model.addAttribute("metodos",    service.ventasPorMetodo(desde, hasta));
        model.addAttribute("clientes",   service.ventasPorCliente(desde, hasta));
        model.addAttribute("inventario", service.inventario());
        model.addAttribute("umbralStock", ReporteService.UMBRAL_STOCK_BAJO);
        model.addAttribute("activeModule", "reportes");
        return "reportes/index";
    }

    // ── Exportación CSV ──────────────────────────────────────────

    @GetMapping("/reportes/export/{tipo}.csv")
    public ResponseEntity<byte[]> exportar(
            @PathVariable String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        LocalDate[] rango = rango(desde, hasta);
        desde = rango[0];
        hasta = rango[1];

        String csv = switch (tipo) {
            case "ventas-dia" -> csvVentasDia(desde, hasta);
            case "productos"  -> csvProductos(desde, hasta);
            case "metodos"    -> csvMetodos(desde, hasta);
            case "clientes"   -> csvClientes(desde, hasta);
            case "inventario" -> csvInventario();
            default -> null;
        };
        if (csv == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] cuerpo = csv.getBytes(StandardCharsets.UTF_8);
        String nombre = "reporte-" + tipo + "-" + desde + "-a-" + hasta + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(cuerpo);
    }

    // ── Construcción de cada CSV ─────────────────────────────────

    private String csvVentasDia(LocalDate desde, LocalDate hasta) {
        List<List<String>> rows = new ArrayList<>();
        for (FilaDia f : service.ventasPorDia(desde, hasta)) {
            rows.add(List.of(f.fecha().toString(), String.valueOf(f.ventas()), money(f.ingresos())));
        }
        return Csv.build(List.of("Fecha", "Ventas", "Ingresos"), rows);
    }

    private String csvProductos(LocalDate desde, LocalDate hasta) {
        List<List<String>> rows = new ArrayList<>();
        for (FilaProducto f : service.productosMasVendidos(desde, hasta)) {
            rows.add(List.of(f.nombre(), String.valueOf(f.unidades()), money(f.ingresos())));
        }
        return Csv.build(List.of("Producto", "Unidades", "Ingresos"), rows);
    }

    private String csvMetodos(LocalDate desde, LocalDate hasta) {
        List<List<String>> rows = new ArrayList<>();
        for (FilaCategoria f : service.ventasPorMetodo(desde, hasta)) {
            rows.add(List.of(f.etiqueta(), String.valueOf(f.ventas()), money(f.ingresos())));
        }
        return Csv.build(List.of("Metodo de pago", "Ventas", "Ingresos"), rows);
    }

    private String csvClientes(LocalDate desde, LocalDate hasta) {
        List<List<String>> rows = new ArrayList<>();
        for (FilaCategoria f : service.ventasPorCliente(desde, hasta)) {
            rows.add(List.of(f.etiqueta(), String.valueOf(f.ventas()), money(f.ingresos())));
        }
        return Csv.build(List.of("Cliente", "Ventas", "Ingresos"), rows);
    }

    private String csvInventario() {
        List<List<String>> rows = new ArrayList<>();
        for (Producto p : service.inventario().productosStockBajo()) {
            rows.add(List.of(p.getNombre(), p.getCategoria(),
                    String.valueOf(p.getStock()), money(p.getPrecio())));
        }
        return Csv.build(List.of("Producto", "Categoria", "Stock", "Precio"), rows);
    }

    // ── Helpers ──────────────────────────────────────────────────

    /** Normaliza el rango: por defecto, del primer día del mes en curso a hoy; ordena si vienen invertidas. */
    private LocalDate[] rango(LocalDate desde, LocalDate hasta) {
        if (desde == null) desde = LocalDate.now().withDayOfMonth(1);
        if (hasta == null) hasta = LocalDate.now();
        if (hasta.isBefore(desde)) {
            LocalDate tmp = desde; desde = hasta; hasta = tmp;
        }
        return new LocalDate[]{desde, hasta};
    }

    private String money(BigDecimal v) {
        return (v != null ? v : BigDecimal.ZERO).toPlainString();
    }
}
