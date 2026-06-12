CREATE TABLE IF NOT EXISTS usuario (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre     VARCHAR(100) NOT NULL,
  email      VARCHAR(120) NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  rol        VARCHAR(20)  NOT NULL,   -- ADMIN | CAJERO
  activo     BOOLEAN      NOT NULL DEFAULT TRUE,
  creado_en  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Usuario admin inicial
-- Contraseña: admin123  (BCrypt hash)
INSERT INTO usuario (nombre, email, password, rol, activo) VALUES
  ('Administrador', 'dofuswcar@gmail.com',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'ADMIN', TRUE);