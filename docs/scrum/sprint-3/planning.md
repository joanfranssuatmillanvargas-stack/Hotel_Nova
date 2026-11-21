# Sprint Planning · R2-Sprint 3 (miércoles 18/11/2026)

**Tarjeta:** D17 · **Facilita:** Joan (SM) · **Decide el alcance:** Jaider (PO) · **Duración:** 1 hora
**Fechas del sprint:** miércoles 18/11 a martes 24/11/2026 · **Review y Retro:** martes 24/11

## Sprint Goal

Al terminar el sprint, el gerente ve el tablero gerencial y aprueba temporadas, los 10 roles están desplegados en
Render con trazabilidad completa y el PO toma la decisión de release.

## Compromiso (8 puntos)

| PBI | Historias | Puntos | Tareas |
|-----|-----------|:------:|--------|
| PBI-07 Gerente | PLA-10 (3), HAB-20 (2) | 5 | T022, T023, T024 |
| PBI-08 Calidad y despliegue | PLA-11 (3) | 3 | T025, T026 |
| PBI-09 Correo (objetivo extendido) | RES-13 (3) | — | T027, T028: **no se inicia** |

**Por qué RES-13 queda fuera:** necesita una cuenta de correo con su contraseña, que es un secreto nuevo que guardar
fuera de GitHub y en Render, y no suma al Sprint Goal. Sus requisitos (REQ-HN-022 y 023) quedan en la matriz de
trazabilidad como "fuera del release".

## Tarjetas del sprint (Trello: HotelNova · R2-Sprint 3)

| Tarjeta | Qué | Responsable | Revisa | Entrega |
|---------|-----|-------------|--------|---------|
| D17 | Sprint Planning S3 y revisión del plan (plan.md v1.2, ADR-008 y 009, OpenAPI v1.2) | Joan | Jaider | mié 18/11 |
| T022 | [P] Pruebas de las temporadas pendientes (`TemporadasTest`) | Joan | Jaider | mié 18/11 |
| T023 | Estado de la temporada y aprobación del gerente | Joan | Jaider | mié 18/11 |
| T024 | Tablero gerencial de solo consulta (`TableroGerenciaTest`, `TarifaPromedioTest`, `PermisosSprint3Test`) | Jaider | Joan | mié 18/11 |
| T025 | Matriz de trazabilidad (`specs/001-hotelnova/trazabilidad.md`) | Jaider | Joan | mié 18/11 |
| T026 | Despliegue en Render y P032 a P034 con los 10 usuarios | Jaider | Joan | jue 19 o vie 20/11 |
| D18 | Sprint Review S3 y decisión de release | Jaider | Joan (facilita) | mar 24/11 |
| D19 | Sprint Retrospective S3 | Joan | Equipo | mar 24/11 |

**Facilitador del Daily:** Joan · **Guardián de la DoD:** Jaider · WIP ≤ 2 tarjetas por persona en *En progreso*.

## Acciones de la Retro del R2-Sprint 2 que se aplican desde hoy

1. El Planning y el código empiezan el día 1 (hoy, 18/11), no el sábado.
2. Las pruebas en Render se hacen a mitad del sprint (jueves 19 o viernes 20/11).
3. Las tarjetas se mueven en Trello el mismo día que se trabajan, pasando por *En progreso*.

## Estado al cierre del día 1 (18/11)

- D17 y T022 a T025 terminadas: código y documentos en la rama `r2-sprint3` (96 de 96 pruebas en verde).
- Falta T026: hacer el merge, desplegar en Render y aprobar P032, P033 y P034.
