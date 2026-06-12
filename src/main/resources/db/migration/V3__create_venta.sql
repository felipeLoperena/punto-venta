CREATE TABLE IF NOT EXISTS venta (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  cliente_nombre VARCHAR(120)  NULL,         -- nombre libre, sin FK por ahora
  metodo_pago  VARCHAR(20)  NOT NULL,        -- EFECTIVO | TARJETA
  total        DECIMAL(12,2) NOT NULL DEFAULT 0,
  creado_en    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;