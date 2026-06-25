# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Levantar el servidor (dev)
mvn spring-boot:run

# Compilar sin tests
mvn package -DskipTests

# Correr todos los tests
mvn test

# Correr un test específico
mvn test -Dtest=NombreDelTest
```

No hay Maven wrapper (`mvnw`); usar `mvn` directamente (Maven 3.9, Java 21 instalados en el sistema).

El servidor corre en `http://localhost:8080`. La base de datos es MySQL local en `punto_venta` (credenciales en `application.yaml`, perfil `local`).

## Arquitectura

Spring Boot 3.5 + Thymeleaf MVC. Sin API REST separada excepto el endpoint `POST /ventas/nueva`, que recibe JSON desde JavaScript del navegador.

### Estructura por módulo

Cada módulo de negocio vive en su propio paquete y sigue la misma forma:

```
<modulo>/
  domain/       Entidad JPA + enums
  repo/         JpaRepository (Spring Data)
  service/      Lógica de negocio, @Transactional
  web/          DTOs/Forms (request/response)
  webui/        @Controller con rutas de página Thymeleaf
```

Los módulos actuales son `producto`, `venta`, `usuario`, `cliente`, `proveedor` y `dashboard`. El paquete `common/error` tiene el `GlobalExceptionHandler` para REST.

El módulo `dashboard` es solo de lectura: agrega métricas desde los repos de `venta` y `producto` (sin entidad propia). Su página vive en la raíz `/` y expone dos endpoints JSON (`/dashboard/api/ventas-diarias`, `/dashboard/api/ventas-mensuales`) que alimentan una gráfica Chart.js (CDN, cargada vía fragmento `head-extra`). Todo el dashboard (`/` y `/dashboard/**`) es solo ADMIN.

### Módulo de ventas

`VentaService.crear()` es transaccional y hace tres cosas en un solo paso: valida stock, construye snapshots de precio en `VentaItem` (nombre y precio se copian del producto en el momento de la venta, no hay FK a precio), y descuenta el stock. `Venta` tiene `items` con `FetchType.EAGER`.

La vista de caja (`ventas/caja.html`) maneja el carrito en memoria del navegador con JavaScript vanilla y lo envía como JSON a `POST /ventas/nueva`. El controlador devuelve `{"ok": true, "ventaId": N}` y el JS redirige a `/ventas/{id}/ticket`. Por eso ese endpoint tiene CSRF deshabilitado en `SecurityConfig`.

### Seguridad

Dos roles: `ADMIN` y `CAJERO`. La autorización está centralizada en `SecurityConfig`:
- `/` (dashboard), `/dashboard/**`, `/usuarios/**`, `/clientes/**`, `/proveedores/**` y `/reportes/**` → solo ADMIN
- `/ventas/**` y `/productos/**` → ADMIN o CAJERO

El ítem "Dashboard" del sidebar (`layout.html`) está envuelto en `sec:authorize="hasRole('ADMIN')"` para que el CAJERO no vea un enlace que le daría 403.

`UsuarioDetailsService` rechaza en login a usuarios con `activo = false`. El campo `activo` hace soft-delete; no hay borrado físico de usuarios. `UsuarioService.toggleActivo()` impide desactivar al único ADMIN activo.

La validación de longitud mínima de contraseña (≥ 6 caracteres) **no está en `UsuarioForm`** — está en el controlador (`UsuarioPageController`) para poder distinguir entre modo crear (obligatoria) y modo editar (opcional).

### Base de datos

Flyway gestiona el esquema. Las migraciones están en `src/main/resources/db/migration/` con el patrón `V{n}__{descripcion}.sql`. Hibernate está en modo `validate`, no toca el esquema. Al agregar una columna o tabla, crear una nueva migración `V{n+1}__...sql`.

El usuario admin inicial se inserta en `V5__create_usuario.sql` (email: `dofuswcar@gmail.com`, password: `admin123`).

### Plantillas

`layout.html` es la plantilla maestra (Thymeleaf Layout Dialect). Cada vista hereda con `layout:decorate="~{layout}"` e inyecta contenido en los fragmentos `breadcrumb` y `content`. Los estilos CSS viven todos en `layout.html` como `<style>` inline; no hay archivos CSS externos propios (solo fuentes de Google y Tabler Icons desde CDN).

Para agregar estilos específicos a una vista usar el fragmento `head-extra`.

El atributo `activeModule` debe pasarse desde cada controlador para que el sidebar marque el ítem activo.
