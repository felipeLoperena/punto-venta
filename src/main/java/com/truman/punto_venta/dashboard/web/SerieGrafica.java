package com.truman.punto_venta.dashboard.web;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serie lista para alimentar Chart.js: etiquetas del eje X y sus valores.
 * {@code labels} y {@code data} tienen siempre el mismo tamaño.
 */
public record SerieGrafica(
        List<String>     labels,
        List<BigDecimal> data
) {}
