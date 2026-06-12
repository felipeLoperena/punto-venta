CREATE TABLE IF NOT EXISTS venta_item (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  venta_id         BIGINT         NOT NULL,
  producto_id      BIGINT         NOT NULL,
  producto_nombre  VARCHAR(80)    NOT NULL,   -- snapshot: nombre al momento de vender
  precio_unitario  DECIMAL(12,2)  NOT NULL,   -- snapshot: precio al momento de vender
  descuento_pct    DECIMAL(5,2)   NOT NULL DEFAULT 0,  -- 0-100
  cantidad         INT            NOT NULL,
  subtotal         DECIMAL(12,2)  NOT NULL,   -- calculado: cantidad * precio * (1 - desc/100)

  CONSTRAINT fk_vi_venta   FOREIGN KEY (venta_id)    REFERENCES venta(id)    ON DELETE CASCADE,
  CONSTRAINT fk_vi_producto FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;