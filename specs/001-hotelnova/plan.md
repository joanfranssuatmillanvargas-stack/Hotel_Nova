# Plan técnico: HotelNova con 10 roles · Versión 1.1 (Planning del R2-Sprint 2)

**PBI:** PBI-01 · **Tarjetas:** D11 (v1.0) y D14 (v1.1) · **Redacta:** Joan Franssuat Millán Vargas · **Revisa:** Jaider Javier Jaimes
**Entra:** `spec.md` v1.0 · **Sale:** este plan, `docs/adr/ADR-001` a `ADR-007` y `api/openapi.yaml` (v1.1)

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
| [ADR-005](../../docs/adr/ADR-005-asignacion-en-la-habitacion.md) | La asignación de limpieza es un campo de `Habitacion` (`asignadaA`) que se borra al salir de LIMPIEZA; el rol de la camarera lo valida `hotel-web` | Colección `asignaciones` aparte |
| [ADR-006](../../docs/adr/ADR-006-pago-del-cajero-y-cierre-calculado.md) | El pago guarda el cajero y el cierre de caja se calcula al consultarlo, por día y cajero; recepción deja de cobrar | Guardar cada cierre en una colección |
| [ADR-007](../../docs/adr/ADR-007-canal-y-quien-toma-la-reserva.md) | Toda reserva exige canal y `tomadaPor`; `hotel-web` los llena según quién reserva | Canal opcional con MOSTRADOR por defecto |

Las decisiones del Sprint 3 (tablero gerencial, temporadas pendientes) se registran como ADR-008 en adelante en su
Planning, cuando se crean sus tarjetas.

## 3. Páginas web y quién entra (hotel-web)

| Ruta | Roles que entran | Cubre |
|------|------------------|-------|
| `/admin/**` | ADMINISTRADOR | Release 1 |
| `/recepcion/**` | RECEPCIONISTA, ADMINISTRADOR | Release 1 |
| `/huesped/**` | HUESPED | Release 1 |
| `/servicios/**` | SERVICIOS, ADMINISTRADOR | Release 1 |
| `/limpieza/**` | LIMPIEZA, ADMINISTRADOR | Release 1 |
| `/gerencia/**` | GERENTE | REQ-HN-002 (S1, provisional); REQ-HN-018 a 021 (S3) |
| `/caja/**` | CAJERO | REQ-HN-002 (S1, provisional); REQ-HN-009 a 012 (S2, `caja_web`) |
| `/mantenimiento/**` | MANTENIMIENTO | REQ-HN-006 a 008 (S1) |
| `/supervision/**` | SUPERVISOR_LIMPIEZA | REQ-HN-002 (S1, provisional); REQ-HN-015 a 017 (S2, `supervision_web`) |
| `/agente/**` | AGENTE_RESERVAS | REQ-HN-002 (S1, provisional); REQ-HN-013 y 014 (S2, `agente_web`) |

La tabla no cambió en el Sprint 2: las rutas nuevas quedaron dentro de los prefijos que ya tenía cada rol.

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

## 5. Componentes del R2-Sprint 2 (Planning, D14)

### ms-servicios-facturacion · PBI-04 Cajero

| Componente | Cambio | Cubre |
|------------|--------|-------|
| `modelo/Pago` | Campo nuevo `cajero` | REQ-HN-009 |
| `modelo/CierreCaja` | Clase nueva: fecha, cajero, totales por medio, total, número de facturas y detalle | REQ-HN-012 |
| `repositorio/factura_repositorio` | `findByEstadoOrderByNumeroAsc` | REQ-HN-009, 012 |
| `repositorio/reglas_repositorio` | `pendientes()`, `pagar(id, medio, cajero)` y `cierre(fecha, cajero)` · ADR-006 | REQ-HN-009, 010, 012 |
| `controlador/factura_apirest` | `GET /api/facturas?estado=PENDIENTE`, `POST /api/facturas/{id}/pago?medio=&cajero=` y `GET /api/facturas/cierre?fecha=&cajero=` | REQ-HN-009, 010, 012 |

### ms-reservas · PBI-05 Agente de reservas

| Componente | Cambio | Cubre |
|------------|--------|-------|
| `modelo/Canal` | Enum nuevo: TELEFONO, CORREO, WHATSAPP, WEB, MOSTRADOR | REQ-HN-013 |
| `modelo/Reserva` | Campos nuevos `canal` y `tomadaPor` | REQ-HN-013 |
| `repositorio/reglas_repositorio` | `crear` exige canal y `tomadaPor` (400) y aplica las reglas de siempre · ADR-007 | REQ-HN-013, RN-07, RN-08 |

### ms-habitaciones · PBI-06 Supervisor de limpieza

| Componente | Cambio | Cubre |
|------------|--------|-------|
| `modelo/Habitacion` | Campos nuevos `asignadaA`, `asignadaPor`, `asignadaEn` | REQ-HN-015 |
| `modelo/Asignacion` y `modelo/PendienteLimpieza` | Clase nueva con `camarera` y `supervisor`; campo `asignadaA` en la lista de limpieza | REQ-HN-015, 017 |
| `repositorio/reglas_repositorio` | `asignar(id, camarera, supervisor)` (409 si no está en LIMPIEZA), `pendientesLimpieza(camarera)` y `cambiarEstado` borra la asignación al salir de LIMPIEZA · ADR-005 | REQ-HN-015, 016, 017 |
| `controlador/habitacion_apirest` | `POST /api/habitaciones/{id}/asignacion` y `GET /api/habitaciones/limpieza?camarera=` | REQ-HN-015 a 017 |

### hotel-web

| Componente | Cambio | Cubre |
|------------|--------|-------|
| `controlador/caja_web` | Bandeja de pendientes, registrar pago y cierre de caja (reemplaza el inicio provisional) | REQ-HN-009 a 012 |
| `controlador/agente_web` | Reservas del agente, nueva reserva con canal, huésped rápido, detalle, modificar y cancelar | REQ-HN-013, 014 |
| `controlador/supervision_web` | Reparto de habitaciones por limpiar entre las camareras | REQ-HN-015, 016 |
| `repositorio/cuentas_repositorio` | `camareras()` y `camarera(usuario)`: usuario activo con rol LIMPIEZA o 409 · ADR-005 | REQ-HN-016 |
| `controlador/limpieza_web` | La camarera ve solo sus habitaciones; el administrador las ve todas | REQ-HN-017 |
| `controlador/recepcion_web` y `fragmentos-estadia.html` | Sin pago (ruta y formulario quitados); la reserva se crea con canal MOSTRADOR | REQ-HN-011, 013 |
| `controlador/huesped_web` | La reserva en línea se crea con canal WEB | REQ-HN-013 |
| `controlador/roles_nuevos_web` | Solo queda el inicio provisional de la gerencia | ADR-004 |
| Plantillas `caja/*`, `agente/*`, `supervision/inicio`, `fragmentos.html` (menús), `limpieza/inicio`, `recepcion/reservas` (columna Canal) | Pantallas del sprint | REQ-HN-009 a 017 |

## 5b. Componentes planeados para el R2-Sprint 3

Se detallan en su Planning; aquí queda el componente responsable de cada requisito.

| Requisito | Componente previsto |
|-----------|---------------------|
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
| REQ-HN-009 a 012 | `ms-servicios-facturacion` (`pagar`, `cierre`), `caja_web` · ADR-006 | 2 |
| REQ-HN-013, 014 | `ms-reservas` (`Canal`, `tomadaPor`), `agente_web` · ADR-007 | 2 |
| REQ-HN-015 a 017 | `ms-habitaciones` (`asignar`, `pendientesLimpieza`), `supervision_web`, `limpieza_web`, `cuentas_repositorio.camarera` · ADR-005 | 2 |
| REQ-HN-018 a 023 | Sección 5b | 3 |
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
| Unitarias (S2) | Pago, bandeja y cierre (`CajaTest`), canal y quién toma la reserva (`AgenteReservasTest`), asignación (`AsignacionTest`), camareras (`CamarerasTest`) y rutas nuevas (`PermisosSprint2Test`) | JUnit 5 + Mockito; se agregó `spring-boot-starter-test` a ms-reservas y ms-servicios-facturacion |
| Plan de pruebas | P021 a P024 (S1) y P025 a P031 (S2) del Excel, a mano sobre la aplicación | Hoja *8. Plan Pruebas* |

Las pruebas se escriben antes que el código (constitución, principio 3) y quedan en `src/test/java` de cada proyecto.

## Acta de la Puerta 2

| Revisión | Cumple |
|----------|:------:|
| Ningún requisito queda sin componente y ningún componente sin requisito | ☑ |
| Cada ADR tiene alternativa descartada y consecuencias | ☑ |
| El OpenAPI lista rutas, roles y códigos de error | ☑ |

**Resultado:** ☑ Aprobada ☐ Aprobada con observaciones ☐ Rechazada
**Revisa:** Jaider Javier Jaimes · **Fecha:** 07/11/2026

## Revisión del plan en el Planning del R2-Sprint 2 (v1.1)

| Revisión | Cumple |
|----------|:------:|
| Cada requisito del Sprint 2 (REQ-HN-009 a 017) tiene componente en la sección 5 y en la matriz | ☑ |
| Cada decisión nueva tiene su ADR (ADR-005 a ADR-007) con alternativa descartada y consecuencias | ☑ |
| El OpenAPI v1.1 describe los endpoints del Sprint 2 como quedaron (rutas, parámetros y errores) | ☑ |
| No se agregó ninguna dependencia nueva fuera de `spring-boot-starter-test` (solo para pruebas) | ☑ |

**Resultado:** ☑ Aprobada ☐ Aprobada con observaciones ☐ Rechazada
**Revisa:** Jaider Javier Jaimes · **Fecha:** 14/11/2026
