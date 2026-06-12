package com.truman.punto_venta.venta.domain;

public enum MetodoPago {
    EFECTIVO,
    TARJETA;

    public String getLabel() {
        return switch (this) {
            case EFECTIVO -> "Efectivo";
            case TARJETA  -> "Tarjeta";
        };
    }
}