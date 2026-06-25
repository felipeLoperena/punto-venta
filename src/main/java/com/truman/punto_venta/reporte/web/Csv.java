package com.truman.punto_venta.reporte.web;

import java.util.List;

/** Construcción mínima de CSV (separador coma, comillas con escape). */
public final class Csv {

    private Csv() {}

    /**
     * Arma un CSV con la cabecera y las filas dadas. Antepone el BOM UTF-8 para
     * que Excel respete los acentos al abrirlo.
     */
    public static String build(List<String> header, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder("﻿");
        sb.append(linea(header));
        for (List<String> row : rows) {
            sb.append(linea(row));
        }
        return sb.toString();
    }

    private static String linea(List<String> campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(escapar(campos.get(i)));
        }
        return sb.append("\r\n").toString();
    }

    private static String escapar(String valor) {
        String v = valor == null ? "" : valor;
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            return '"' + v.replace("\"", "\"\"") + '"';
        }
        return v;
    }
}
