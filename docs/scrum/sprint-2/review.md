# Sprint Review · R2-Sprint 2 (martes 17/11/2026)

**Tarjeta:** D15 · **Facilita:** Joan (SM) · **Presenta:** Jaider (PO) · **Stakeholder:** docente (gerencia del hotel)
**Duración:** 45 minutos · **Sprint Goal:** al terminar el sprint, un agente puede tomar reservas por teléfono, el cajero
cobra y cierra la caja del día, y el supervisor reparte las habitaciones entre las camareras.

> Preparado el 14/11 con el estado del tablero de ese día. Lo que está entre corchetes se llena en la Review.

## 1. Guion de la demo (15 minutos)

Antes de empezar: la URL de Render despierta (los 5 servicios) y una segunda camarera creada por `admin` en Usuarios
(por ejemplo `ana` / `ana123`, rol Personal de limpieza).

| # | Quién | Qué se hace | Qué debe verse | Cubre |
|---|-------|-------------|----------------|-------|
| 1 | `agente` | Nueva reserva → documento de un huésped que no existe, canal TELÉFONO → "Buscar huésped" | Aparece el formulario para registrarlo sin colgar | RES-12 |
| 2 | `agente` | Registrar el huésped → buscar fechas → Reservar la 101 | Reserva confirmada con canal TELEFONO y tomada por `agente` | AC-013.1 |
| 3 | `agente` | Intentar otra reserva de la 101 que se cruce con la anterior | Mensaje rojo de fechas cruzadas | AC-013.2 |
| 4 | `agente` | Abrir el detalle de la reserva | Solo modificar y cancelar: sin check-in, check-out ni pagos | AC-014.1 |
| 5 | `agente` | Escribir en la barra `/recepcion/reservas` | Vuelve a su inicio | AC-014.1 |
| 6 | `recepcion` | Check-in de una reserva de hoy → check-out | Se genera la factura PENDIENTE y la estadía dice "El pago lo registra el cajero" | AC-011.1 |
| 7 | `recepcion` | Escribir en la barra `/caja` | Vuelve a recepción | AC-011.1 |
| 8 | `cajero` | "Por cobrar" → cobrar esa factura en EFECTIVO | Mensaje verde; la factura sale de la bandeja | AC-009.1 |
| 9 | Swagger de ms-servicios | `POST /api/facturas/{id}/pago` sobre la misma factura | 409: la factura ya está pagada | AC-010.1 |
| 10 | `cajero` | "Cierre de caja" de hoy y de un día sin pagos | Totales por medio de pago; el día sin pagos en $0 | AC-012.1, 012.2 |
| 11 | `supervisor` | Asignar la habitación del check-out a `limpieza` → reasignarla a `ana` | La etiqueta cambia de camarera | AC-015.1, 015.2 |
| 12 | `limpieza` y `ana` | Abrir el inicio de cada una | Solo `ana` ve esa habitación | AC-017.1 |
| 13 | Swagger de ms-habitaciones | `POST /api/habitaciones/{id}/asignacion` sobre una habitación OCUPADA | 409: solo se asignan habitaciones en LIMPIEZA | AC-016.1 |
| 14 | — | Mostrar plan.md v1.1, ADR-005 a 007, tasks.md y las pruebas en verde (71 de 71) | Trazabilidad del sprint | PBI-04 a 06 |

## 2. Qué se terminó y qué no

| PBI | Puntos | ¿Terminado? | Comentario |
|-----|:------:|:-----------:|------------|
| PBI-04 Cajero (SER-11, SER-12) | 5 | [Sí / No] | Al 14/11: T012 a T015 terminadas; faltan P025 a P027 en Render |
| PBI-05 Agente de reservas (RES-11, RES-12) | 5 | [Sí / No] | Al 14/11: T016 a T018 terminadas; faltan P028 y P029 en Render |
| PBI-06 Supervisor de limpieza (HAB-18, HAB-19) | 3 | [Sí / No] | Al 14/11: T019 a T021 terminadas; faltan P030 y P031 en Render |
| **Total** | **13** | **[__] de 13** | |

## 3. Decisiones que se muestran al docente

| # | Decisión | Dónde queda | Respuesta del docente |
|---|----------|-------------|-----------------------|
| 1 | Cada cajero cierra su propia caja; el cierre se calcula, no se guarda | ADR-006 | [__] |
| 2 | Toda reserva exige canal; las del Release 1 quedan "Sin registrar" | ADR-007 | [__] |
| 3 | Desde este sprint la camarera ve solo lo que le asignan (el usuario `limpieza` también) | ADR-005 | [__] |

## 4. Comentarios del stakeholder

- [__]

Si aparece una solicitud de cambio, entra al backlog como **PBI-10 (CR-001)** y la decide el PO.

## 5. Impedimentos

- IMP-03: el sprint empezó después del día 3 (igual que IMP-02). Estado: [Abierto / Cerrado].
