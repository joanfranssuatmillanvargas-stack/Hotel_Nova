# Sprint Review · R2-Sprint 1 (martes 10/11/2026)

**Tarjeta:** D13 · **Facilita:** Joan (SM) · **Presenta:** Jaider (PO) · **Stakeholder:** docente (gerencia del hotel)
**Duración:** 45 minutos · **Sprint Goal:** al terminar el sprint, la spec v1.0 está aprobada con su plan y tareas,
los 10 roles inician sesión cada uno en su inicio, y un técnico puede reparar una habitación dañada y devolverla al servicio.

> Se hizo el martes 10/11. Registro escrito el 14/11 con lo que contó el equipo.

## 1. Guion de la demo (15 minutos)

Antes de empezar: los 5 proyectos corriendo (o la URL de Render despierta) y "Cargar datos de ejemplo" hecho desde `admin`.

| # | Quién | Qué se hace | Qué debe verse | Cubre |
|---|-------|-------------|----------------|-------|
| 1 | `admin` | Usuarios y roles → Nuevo usuario → abrir la lista de roles | Aparecen los 10 roles | AC-001.1 |
| 2 | `gerente`, `cajero`, `supervisor`, `agente` | Iniciar sesión con cada uno (contraseña = usuario + 123) | Cada uno llega a su inicio, con su menú y lo que le llega en su sprint | AC-002.1 |
| 3 | `cajero` | Escribir en la barra `/recepcion/reservas` | Vuelve a Caja sin ver reservas | AC-004.1 |
| 4 | `admin` | Escribir `/mantenimiento` | Vuelve al panel del administrador | AC-004.3 |
| 5 | `limpieza` | Reportar un daño en una habitación DISPONIBLE (ej. 204: "grifo del baño goteando") | La 204 pasa a mantenimiento | Release 1 (HAB-13) |
| 6 | `mantenimiento` | Abrir su inicio | Ve la 204 con el daño, quién lo reportó y cuándo | AC-006.1 |
| 7 | `mantenimiento` | Escribir "se cambió el empaque del grifo" → Registrar reparación | Mensaje verde; la 204 sale de su lista | AC-007.1 |
| 8 | `limpieza` | Abrir su inicio | La 204 aparece por limpiar; en "Registros de hoy" está la reparación | AC-007.1 |
| 9 | Swagger de ms-habitaciones | `POST /api/habitaciones/{id}/reparacion` sobre una habitación DISPONIBLE | 409 con el mensaje del estado | AC-008.1 |
| 10 | — | Mostrar spec.md, plan.md, tasks.md y las pruebas en verde (`mvn test`) | Las 3 puertas con su acta | PBI-01 |

## 2. Qué se terminó y qué no

| PBI | Puntos | ¿Terminado? | Comentario |
|-----|:------:|:-----------:|------------|
| PBI-01 Spec v1.0, plan y tareas | 5 | Sí | Puertas 1, 2 y 3 aprobadas el 07/11/2026 (D10 a D12) |
| PBI-02 Base de seguridad para 10 roles | 5 | Sí | PLA-08 y PLA-09: T001 a T006 terminadas el 07/11 |
| PBI-03 Técnico de mantenimiento | 3 | Sí | HAB-16 y HAB-17: T007 a T011 terminadas; P021 a P024 aprobadas en Render el 09/11 |
| **Total** | **13** | **13 de 13** | El docente vio la demo y todo funcionó |

## 3. Decisiones que se confirman con el docente

| # | Pregunta (spec.md, sección 10) | Decisión del PO | Respuesta del docente |
|---|--------------------------------|-----------------|-----------------------|
| 1 | Al reparar, ¿la habitación va a LIMPIEZA o queda DISPONIBLE? | A LIMPIEZA | Confirmada: aprobó el funcionamiento como está |
| 5 | ¿La recepcionista sigue cobrando hasta que exista el cajero? | Sí, hasta el R2-Sprint 2 | Confirmada: aprobó el funcionamiento como está |

## 4. Comentarios del stakeholder

- El docente revisó el incremento, vio que todo funcionaba y dijo que el funcionamiento estaba bien así.
- No pidió cambios: no entra ninguna solicitud de cambio (CR) al backlog.

Si aparece una solicitud de cambio, entra al backlog como **PBI-10 (CR-001)** y la decide el PO (no se construye en este sprint).

## 5. Impedimentos

- IMP-02: la especificación empezó tres días tarde. Estado: Cerrado el 07/11/2026 (las tres puertas aprobadas). Causa: disponibilidad de tiempo; el trabajo arranca casi siempre el sábado.
