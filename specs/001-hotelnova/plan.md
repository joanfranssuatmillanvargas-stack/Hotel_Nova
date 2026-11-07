# Plan técnico: HotelNova con 10 roles · Versión 1.0 (para la Puerta 2)

**PBI:** PBI-01 · **Tarjeta:** D11 · **Redacta:** Joan Franssuat Millán Vargas · **Revisa:** Jaider Javier Jaimes
**Entra:** `spec.md` v1.0 · **Sale:** este plan, `docs/adr/ADR-001` a `ADR-004` y `api/openapi.yaml`

## 1. Lo que no cambia

El Release 2 se construye sobre la arquitectura del Release 1, sin cambiarla:

| Aspecto | Decisión (vigente desde el Release 1) |
|---------|---------------------------------------|
| Lenguaje y marco | Java 21 · Spring Boot 4.1.1 |
| Arquitectura | 4 microservicios REST independientes + `hotel-web` (Thymeleaf + Bootstrap) que los consume por REST |
| Datos | MongoDB Atlas, una base por microservicio; `usuariosdb` para los usuarios de `hotel-web` |
| Paquetes por proyecto | `controlador`, `modelo`, `repositorio` (reglas de negocio en `reglas_repositorio`) |
| Errores de negocio | `ResponseStatusException`: 400 dato inválido, 404 no existe, 409 regla de negocio |
| Contraseñas | PBKDF2 con sal aleatoria (`cuentas_repositorio`) |
| Despliegue | Docker en Render con `render.yaml` |
| Contrato | OpenAPI con springdoc en cada microservicio (`/swagger-ui.html`) |

## 2. Decisiones nuevas (ADR)

| ADR | Decisión | Alternativa descartada |
|-----|----------|------------------------|
| [ADR-001](../../docs/adr/ADR-001-roles-y-permisos-por-ruta.md) | Los 5 roles nuevos se agregan al enum `Rol` de `hotel-web`, cada uno con su ruta de inicio, y los permisos se siguen controlando por prefijo de ruta en `FiltroSesion` | Migrar a Spring Security a mitad del release |
| [ADR-002](../../docs/adr/ADR-002-usuarios-de-prueba-idempotentes.md) | Los usuarios de prueba se crean al arrancar, uno por uno, solo si su usuario no existe | Crearlos a mano en Atlas |
| [ADR-003](../../docs/adr/ADR-003-reparacion-como-novedad.md) | La reparación es un tipo nuevo de novedad (`REPARACION`) en la colección `novedades` de `ms-habitaciones` | Una colección `reparaciones` aparte |
| [ADR-004](../../docs/adr/ADR-004-inicio-provisional-de-roles.md) | Los roles del Sprint 2 y 3 tienen desde el Sprint 1 un inicio provisional que muestra qué llega y cuándo | No crear sus rutas hasta su sprint |

Las decisiones del Sprint 2 y 3 (asignaciones, cierre de caja, temporadas pendientes) se registran como ADR-005 en
adelante en el Planning de cada sprint, cuando se crean sus tarjetas.

## 3. Páginas web y quién entra (hotel-web)

| Ruta | Roles que entran | Cubre |
|------|------------------|-------|
| `/admin/**` | ADMINISTRADOR | Release 1 |
| `/recepcion/**` | RECEPCIONISTA, ADMINISTRADOR | Release 1 |
| `/huesped/**` | HUESPED | Release 1 |
| `/servicios/**` | SERVICIOS, ADMINISTRADOR | Release 1 |
| `/limpieza/**` | LIMPIEZA, ADMINISTRADOR | Release 1 |
| `/gerencia/**` | GERENTE | REQ-HN-002 (S1, provisional); REQ-HN-018 a 021 (S3) |
| `/caja/**` | CAJERO | REQ-HN-002 (S1, provisional); REQ-HN-009 a 012 (S2) |
| `/mantenimiento/**` | MANTENIMIENTO | REQ-HN-006 a 008 (S1) |
| `/supervision/**` | SUPERVISOR_LIMPIEZA | REQ-HN-002 (S1, provisional); REQ-HN-015 a 017 (S2) |
| `/agente/**` | AGENTE_RESERVAS | REQ-HN-002 (S1, provisional); REQ-HN-013 y 014 (S2) |

Toda ruta que no sea `/login`, `/registro`, `/salir` o un archivo estático exige sesión (REQ-HN-005).

## 4. Componentes del R2-Sprint 1

### hotel-web

| Componente | Cambio | Cubre |
|------------|--------|-------|
| `modelo/Rol` | 5 valores nuevos con etiqueta y ruta de inicio: `GERENTE` (/gerencia), `CAJERO` (/caja), `MANTENIMIENTO` (/mantenimiento), `SUPERVISOR_LIMPIEZA` (/supervision), `AGENTE_RESERVAS` (/agente) | REQ-HN-001, 002 |
| `controlador/FiltroSesion` | Tabla de permisos con las 10 rutas de la sección 3 | REQ-HN-004, 005 |
| `repositorio/cuentas_repositorio` | `crearUsuariosIniciales()` recorre la lista de usuarios de prueba y crea solo los que falten | REQ-HN-003 |
| `controlador/mantenimiento_web` | Inicio del técnico (lista) y acción "Registrar reparación" | REQ-HN-006, 007, 008 |
| `controlador/roles_nuevos_web` | Inicio provisional de gerente, cajero, supervisor y agente | REQ-HN-002 |
| `templates/fragmentos.html` | Un bloque de menú por cada rol nuevo | REQ-HN-002 |
| `templates/mantenimiento/inicio.html` | Pantalla del técnico, pensada para el celular | REQ-HN-006, NFR-HN-003 |
| `templates/rol-provisional.html` | Pantalla común de los 4 inicios provisionales | REQ-HN-002 |
| `templates/limpieza/inicio.html` y `admin/reportes.html` | Mostrar la novedad "Reparación" con su color | REQ-HN-007 |

`admin/usuarios.html` no cambia: ya lista `Rol.values()`, así que muestra los 10 roles (REQ-HN-001).

### ms-habitaciones

| Componente | Cambio | Cubre |
|------------|--------|-------|
| `modelo/TipoNovedad` | Valor nuevo `REPARACION` | REQ-HN-007 |
| `modelo/HabitacionMantenimiento` | Clase nueva: habitación en MANTENIMIENTO con su último daño | REQ-HN-006 |
| `modelo/ReporteReparacion` | Clase nueva: lo que envía el técnico (`tecnico`, `descripcion`) | REQ-HN-007 |
| `repositorio/reglas_repositorio` | `enMantenimiento()` y `registrarReparacion(id, tecnico, descripcion)` | REQ-HN-006, 007, 008 |
| `controlador/habitacion_apirest` | `GET /api/habitaciones/mantenimiento` y `POST /api/habitaciones/{id}/reparacion` | REQ-HN-006, 007, 008 |

Errores de la reparación: 400 si falta el técnico o la descripción; 404 si la habitación no existe; 409 si no está en
MANTENIMIENTO (REQ-HN-008), con el mensaje "La habitación 101 está DISPONIBLE: solo se registran reparaciones de
habitaciones en MANTENIMIENTO".

## 5. Componentes planeados para el R2-Sprint 2 y 3

Se detallan en el Planning de cada sprint; aquí queda el componente responsable de cada requisito.

| Requisito | Componente previsto |
|-----------|---------------------|
| REQ-HN-009 a 011 Pago por el cajero | `ms-servicios-facturacion`: `POST /api/facturas/{id}/pago` guarda `cajero`; `hotel-web`: `caja_web`; se quita el pago de `recepcion_web` |
| REQ-HN-012 Cierre de caja | `ms-servicios-facturacion`: `GET /api/facturas/cierre?fecha=` |
| REQ-HN-013 Canal y agente | `ms-reservas`: campos `canal` y `tomadaPor` en `Reserva`; `hotel-web`: `agente_web` |
| REQ-HN-014 Agente sin check-in | `hotel-web`: `agente_web` sin esas acciones; `FiltroSesion` no le da `/recepcion` |
| REQ-HN-015 a 017 Asignaciones | `ms-habitaciones`: campo `asignadaA` en `Habitacion`, `POST /api/habitaciones/{id}/asignacion`, `GET /api/habitaciones/limpieza?camarera=` |
| REQ-HN-018 y 019 Tablero gerencial | `hotel-web`: `gerencia_web` que consulta los reportes que ya existen en los 4 microservicios |
| REQ-HN-020 y 021 Temporadas | `ms-habitaciones`: campo `estado` en `Temporada` (PENDIENTE, APROBADA, RECHAZADA) y `POST /api/temporadas/{id}/aprobacion`; la tarifa solo usa las APROBADAS |
| REQ-HN-022 y 023 Correo | `ms-reservas`: envío con reintento registrado; si falla, se guarda el error (objetivo extendido) |

## 6. Matriz requisito → componente (Puerta 2)

| Requisito | Componente | Sprint |
|-----------|------------|:------:|
| REQ-HN-001 | `Rol` (hotel-web) · ADR-001 | 1 |
| REQ-HN-002 | `Rol`, `fragmentos.html`, `roles_nuevos_web` · ADR-004 | 1 |
| REQ-HN-003 | `cuentas_repositorio.crearUsuariosIniciales` · ADR-002 | 1 |
| REQ-HN-004, 005 | `FiltroSesion` · ADR-001 | 1 |
| REQ-HN-006 | `reglas_repositorio.enMantenimiento`, `GET /api/habitaciones/mantenimiento`, `mantenimiento_web` | 1 |
| REQ-HN-007, 008 | `reglas_repositorio.registrarReparacion`, `POST /api/habitaciones/{id}/reparacion`, `mantenimiento_web` · ADR-003 | 1 |
| REQ-HN-009 a 023 | Sección 5 | 2 y 3 |
| NFR-HN-001 | `cuentas_repositorio` (PBKDF2, sin cambios) | 1 |
| NFR-HN-002 | `FiltroSesion` + matriz de permisos | 1 |
| NFR-HN-003 | `mantenimiento/inicio.html` con Bootstrap | 1 |
| NFR-HN-004, 005 | Despliegue en Render y matriz de trazabilidad (PLA-11) | 3 |

Ningún requisito queda sin componente y ningún componente nuevo queda sin requisito.

## 7. Pruebas

| Nivel | Qué | Herramienta |
|-------|-----|-------------|
| Unitarias | Reglas de la reparación y de la lista de mantenimiento (`reglas_repositorio`) con los repositorios simulados | JUnit 5 + Mockito (`spring-boot-starter-test`) |
| Unitarias | Permisos por ruta para los 10 roles (`FiltroSesion`) y usuarios de prueba (`cuentas_repositorio`) | JUnit 5 + Mockito |
| Plan de pruebas | P021 a P024 del Excel, a mano sobre la aplicación | Hoja *8. Plan Pruebas* |

Las pruebas se escriben antes que el código (constitución, principio 3) y quedan en `src/test/java` de cada proyecto.

## Acta de la Puerta 2

| Revisión | Cumple |
|----------|:------:|
| Ningún requisito queda sin componente y ningún componente sin requisito | ☐ |
| Cada ADR tiene alternativa descartada y consecuencias | ☐ |
| El OpenAPI lista rutas, roles y códigos de error | ☐ |

**Resultado:** ☐ Aprobada ☐ Aprobada con observaciones ☐ Rechazada
**Revisa:** Jaider Javier Jaimes · **Fecha:** ____/11/2026
