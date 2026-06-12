package com.truman.punto_venta.usuario.domain;

public enum Rol {
    ADMIN,
    CAJERO;

    public String getLabel() {
        return switch (this) {
            case ADMIN  -> "Administrador";
            case CAJERO -> "Cajero";
        };
    }
}