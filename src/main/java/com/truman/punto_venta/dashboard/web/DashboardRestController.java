package com.truman.punto_venta.dashboard.web;

import com.truman.punto_venta.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/dashboard/api")
public class DashboardRestController {

    private final DashboardService dashboardService;

    public DashboardRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /** Serie de ingresos día a día de un mes (por defecto, el mes en curso). */
    @GetMapping("/ventas-diarias")
    public SerieGrafica ventasDiarias(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes) {
        LocalDate hoy = LocalDate.now();
        return dashboardService.ingresosPorDia(
                anio != null ? anio : hoy.getYear(),
                mes  != null ? mes  : hoy.getMonthValue());
    }

    /** Serie de ingresos mes a mes de los últimos N meses (por defecto, 12). */
    @GetMapping("/ventas-mensuales")
    public SerieGrafica ventasMensuales(
            @RequestParam(defaultValue = "12") int meses) {
        return dashboardService.ingresosPorMes(meses);
    }
}
