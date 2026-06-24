package com.truman.punto_venta.dashboard.service;

import com.truman.punto_venta.dashboard.web.DashboardResumen;
import com.truman.punto_venta.dashboard.web.SerieGrafica;
import com.truman.punto_venta.producto.repo.ProductoRepository;
import com.truman.punto_venta.venta.repo.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DashboardService {

    /** Umbral por debajo del cual un producto se considera con stock bajo. */
    public static final int UMBRAL_STOCK_BAJO = 5;

    private final VentaRepository    ventaRepository;
    private final ProductoRepository productoRepository;

    public DashboardService(VentaRepository ventaRepository,
                            ProductoRepository productoRepository) {
        this.ventaRepository    = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResumen resumen() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        return new DashboardResumen(
                ventaRepository.countByFechaGreaterThanEqual(inicioHoy),
                ventaRepository.sumTotalDesde(inicioHoy),
                ventaRepository.sumTotalDesde(inicioMes),
                productoRepository.countByActivoTrue(),
                productoRepository.countByActivoTrueAndStockLessThanEqual(UMBRAL_STOCK_BAJO),
                ventaRepository.findTop5ByOrderByFechaDesc(),
                productoRepository.findTop5ByActivoTrueAndStockLessThanEqualOrderByStockAsc(UMBRAL_STOCK_BAJO)
        );
    }

    /** Ingresos día a día de un mes; los días sin ventas aparecen en 0. */
    @Transactional(readOnly = true)
    public SerieGrafica ingresosPorDia(int anio, int mes) {
        Map<Integer, BigDecimal> porDia = new HashMap<>();
        for (Object[] fila : ventaRepository.ingresosPorDia(anio, mes)) {
            porDia.put(((Number) fila[0]).intValue(), (BigDecimal) fila[1]);
        }

        int dias = YearMonth.of(anio, mes).lengthOfMonth();
        List<String>     labels = new ArrayList<>(dias);
        List<BigDecimal> data   = new ArrayList<>(dias);
        for (int d = 1; d <= dias; d++) {
            labels.add(String.valueOf(d));
            data.add(porDia.getOrDefault(d, BigDecimal.ZERO));
        }
        return new SerieGrafica(labels, data);
    }

    /** Ingresos mes a mes de los últimos {@code meses}; los meses sin ventas aparecen en 0. */
    @Transactional(readOnly = true)
    public SerieGrafica ingresosPorMes(int meses) {
        Map<YearMonth, BigDecimal> porMes = new HashMap<>();
        YearMonth desde = YearMonth.now().minusMonths(meses - 1L);
        for (Object[] fila : ventaRepository.ingresosPorMes(desde.atDay(1).atStartOfDay())) {
            YearMonth ym = YearMonth.of(((Number) fila[0]).intValue(), ((Number) fila[1]).intValue());
            porMes.put(ym, (BigDecimal) fila[2]);
        }

        List<String>     labels = new ArrayList<>(meses);
        List<BigDecimal> data   = new ArrayList<>(meses);
        for (int i = 0; i < meses; i++) {
            YearMonth ym = desde.plusMonths(i);
            String nombreMes = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.of("es", "ES"));
            labels.add(nombreMes + " " + ym.getYear());
            data.add(porMes.getOrDefault(ym, BigDecimal.ZERO));
        }
        return new SerieGrafica(labels, data);
    }
}
