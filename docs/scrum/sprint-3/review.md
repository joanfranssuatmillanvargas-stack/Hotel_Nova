# Sprint Review · R2-Sprint 3 (martes 24/11/2026)

**Tarjeta:** D18 · **Facilita:** Joan (SM) · **Presenta:** Jaider (PO) · **Stakeholder:** docente (gerencia del hotel)
**Duración:** 45 minutos · **Sprint Goal:** al terminar el sprint, el gerente ve el tablero gerencial y aprueba
temporadas, los 10 roles están desplegados en Render con trazabilidad completa y el PO toma la decisión de release.

> Preparado el 18/11 y actualizado el 21/11 con las pruebas en Render (P032 a P034 aprobadas). Lo que está entre corchetes se llena en la Review.

## 1. Guion de la demo (15 minutos)

Antes de empezar: la URL de Render despierta (los 5 servicios).

| # | Quién | Qué se hace | Qué debe verse | Cubre |
|---|-------|-------------|----------------|-------|
| 1 | `admin` | Habitaciones → Nueva temporada para el tipo Doble, del 01/03/2027 al 31/03/2027, a una tarifa mayor que la base (no usar 15/12 a 15/01: ya hay una Doble aprobada en esas fechas y se cruzaría) | Mensaje "queda PENDIENTE"; en la tabla aparece PENDIENTE | AC-020.1 |
| 2 | `recepcion` | Nueva reserva de una Doble del 10/03/2027 al 11/03/2027 (sin guardarla) | El valor de la noche sigue en la tarifa base | AC-020.1 |
| 3 | `gerente` | Abrir su inicio | Aviso de 1 temporada pendiente; ocupación de hoy, tarifa promedio e ingresos del mes por categoría | AC-018.1 |
| 4 | `gerente` | Recorrer el tablero | No hay botones para crear, editar ni cobrar | AC-019.1 |
| 5 | `gerente` | Escribir en la barra `/admin/habitaciones` y luego `/caja` | Vuelve a Gerencia las dos veces | AC-019.1 |
| 6 | `gerente` | Temporadas → Aprobar | Queda APROBADA con quién y cuándo | AC-021.1 |
| 7 | `recepcion` | Repetir la búsqueda del paso 2 | La noche del 10/03/2027 ya cuesta la tarifa de la temporada | AC-021.1 |
| 8 | Swagger de ms-habitaciones | `POST /api/temporadas/{id}/aprobacion` sobre la misma temporada | 409: ya está APROBADA | REQ-HN-021 |
| 9 | `admin` + `gerente` | Crear otra temporada (por ejemplo Suite, del 01/04/2027 al 30/04/2027) y rechazarla | Queda RECHAZADA y la tarifa no cambia | AC-021.2 |
| 10 | Los 10 usuarios | Iniciar sesión uno por uno | Cada uno entra a su inicio con su menú | NFR-HN-004 |
| 11 | — | Mostrar `trazabilidad.md`, ADR-008 a 010, las pruebas en verde (96 de 96) y las capturas de `evidencias/` | Todo requisito tiene criterio y prueba | NFR-HN-005 |
| 12 | — | Mostrar las 4 gráficas de seguimiento (`docs/scrum/graficas/HotelNova_Graficas_Seguimiento.pdf`) | Burndown en 0, estado sobre la diagonal, flujo acumulado y 34 OK / 0 KO | Herramientas de proyectos, cap. 8 |

## 2. Qué se terminó y qué no

| PBI | Puntos | ¿Terminado? | Comentario |
|-----|:------:|:-----------:|------------|
| PBI-07 Gerente (PLA-10, HAB-20) | 5 | Sí | T022 a T024 el 18/11; P032 y P033 aprobadas en Render el 21/11 |
| PBI-08 Calidad y despliegue (PLA-11) | 3 | Sí | T025 el 18/11; T026 (P034) y T029 (diseño, ADR-010) el 21/11 |
| PBI-09 Correo (RES-13) | — | No | Objetivo extendido: no se inició (decisión del Planning) |
| **Total** | **8** | **8 de 8** | |

## 3. Decisiones que se muestran al docente

| # | Decisión | Dónde queda | Respuesta del docente |
|---|----------|-------------|-----------------------|
| 1 | Las temporadas del Release 1 (sin estado) siguen vigentes como aprobadas | ADR-008 | [__] |
| 2 | Editar una temporada aprobada la devuelve a PENDIENTE (mientras tanto se cobra la tarifa base) | ADR-008 | [__] |
| 3 | La tarifa promedio = alojamiento vendido / noches vendidas de las reservas no canceladas del periodo | ADR-009 | [__] |
| 4 | RES-13 (correo) no entra al release | planning.md | [__] |
| 5 | Diseño nuevo de las pantallas (solo estilo, sin cambiar funciones) | ADR-010 | [__] |

## 4. Decisión de release (PO)

- [ ] Se libera el Release 2 · [ ] No se libera (motivo: [__])

## 5. Comentarios del stakeholder

- [__]
