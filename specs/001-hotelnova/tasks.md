# Tareas: HotelNova con 10 roles · Versión 1.0 (para la Puerta 3)

**PBI:** PBI-01 · **Tarjeta:** D12 · **Redacta:** Jaider Javier Jaimes · **Revisa:** Joan Franssuat Millán Vargas
**Entra:** `spec.md` v1.0, `plan.md` y ADR-001 a ADR-004

Cada línea es una tarea que cabe en un pull request y se vuelve una tarjeta T### en Trello. Entre paréntesis: lo que
cubre y de qué tarea depende. `[P]` = se puede hacer en paralelo. `[PLA-08]` = historia a la que pertenece.
Quién la hace y cuándo lo decide el equipo en Trello y en la hoja Tareas_SDD, no aquí.

Las tarjetas se crean *just-in-time*: T001 a T011 al aprobar esta Puerta 3 (R2-Sprint 1); T012 a T021 en el
Planning del R2-Sprint 2; T022 a T028 en el del R2-Sprint 3.

## Fase 0 · Preparación (R2-Sprint 1)
- [x] T001 Agregar `spring-boot-starter-test` a hotel-web y ms-habitaciones (plan §7)

## Fase 1 · PBI-02 Base de seguridad para 10 roles (R2-Sprint 1)
- [x] T002 [P] [PLA-08] Pruebas de los roles y de los usuarios de prueba (AC-001.1, AC-003.1 a 003.3) (después de T001)
- [x] T003 [PLA-08] Roles nuevos en `Rol` y `crearUsuariosIniciales` que crea solo los que faltan (REQ-HN-001, 003 · ADR-001, 002) (después de T002)
- [x] T004 [P] [PLA-09] Pruebas de `FiltroSesion` para los 10 roles (AC-004.1 a 004.4, AC-005.1) (después de T001)
- [x] T005 [PLA-09] Tabla de permisos con las 10 rutas en `FiltroSesion` (REQ-HN-004, 005 · ADR-001) (después de T003 y T004)
- [x] T006 [PLA-08] Menú de cada rol nuevo e inicio provisional de gerente, cajero, supervisor y agente (REQ-HN-002, AC-002.1, 002.2 · ADR-004) (después de T005)

## Fase 2 · PBI-03 Técnico de mantenimiento (R2-Sprint 1)
- [x] T007 [P] [HAB-16] Pruebas de la lista de mantenimiento (AC-006.1 a 006.3) (después de T001)
- [x] T008 [P] [HAB-17] Pruebas del registro de la reparación (AC-007.1, 007.2, 008.1, 008.2) (después de T001)
- [x] T009 [HAB-16] [HAB-17] `REPARACION`, `HabitacionMantenimiento`, `ReporteReparacion`, reglas y los 2 endpoints en ms-habitaciones (REQ-HN-006 a 008 · ADR-003) (después de T007 y T008)
- [x] T010 [HAB-16] [HAB-17] Pantalla del técnico en hotel-web y novedad "Reparación" en limpieza y reportes (REQ-HN-006 a 008, NFR-HN-003) (después de T006 y T009)

## Fase 3 · Cierre del R2-Sprint 1
- [ ] T011 Ejecutar P021 a P024 del plan de pruebas y actualizar LEEME.txt con los 10 usuarios (NFR-HN-002) (después de T010)

**Estado al 07/11/2026:** T001 a T010 terminadas (las pruebas unitarias pasan: 32 de 32). T011 en pruebas: faltan P023 y P024 en Render.

Punto de control del R2-Sprint 1: los 10 usuarios de prueba inician sesión cada uno en su inicio, y una habitación
dañada pasa de MANTENIMIENTO a LIMPIEZA con la reparación registrada.

## Fase 4 · PBI-04 Cajero (R2-Sprint 2)
- [ ] T012 [P] [SER-11] [SER-12] Pruebas del pago por el cajero y del cierre de caja (AC-009.1, 010.1, 012.1, 012.2)
- [ ] T013 [SER-11] Pago con medio y cajero en ms-servicios-facturacion; quitar el pago de recepción (REQ-HN-009 a 011) (después de T012)
- [ ] T014 [SER-12] Cierre de caja por día (REQ-HN-012) (después de T013)
- [ ] T015 [SER-11] [SER-12] Pantalla del cajero (AC-011.1) (después de T014)

## Fase 5 · PBI-05 Agente de reservas (R2-Sprint 2)
- [ ] T016 [P] [RES-11] [RES-12] Pruebas del canal y de los permisos del agente (AC-013.1, 013.2, 014.1)
- [ ] T017 [RES-11] Canal y `tomadaPor` en las reservas de ms-reservas (REQ-HN-013) (después de T016)
- [ ] T018 [RES-12] Pantalla del agente: huésped rápido, reservar, modificar y cancelar (REQ-HN-014) (después de T017)

## Fase 6 · PBI-06 Supervisor de limpieza (R2-Sprint 2)
- [ ] T019 [P] [HAB-18] [HAB-19] Pruebas de la asignación (AC-015.1, 015.2, 016.1, 017.1)
- [ ] T020 [HAB-18] Asignación y reasignación en ms-habitaciones (REQ-HN-015, 016) (después de T019)
- [ ] T021 [HAB-18] [HAB-19] Pantalla del supervisor y lista "mis habitaciones" de la camarera (REQ-HN-017) (después de T020)

## Fase 7 · PBI-07 Gerente (R2-Sprint 3)
- [ ] T022 [P] [HAB-20] Pruebas de las temporadas pendientes (AC-020.1, 021.1, 021.2)
- [ ] T023 [HAB-20] Estado de la temporada y aprobación del gerente (REQ-HN-020, 021) (después de T022)
- [ ] T024 [PLA-10] Tablero gerencial de solo consulta (REQ-HN-018, 019, AC-018.1, 019.1)

## Fase 8 · PBI-08 Calidad y despliegue (R2-Sprint 3)
- [ ] T025 [PLA-11] Matriz de trazabilidad completa: requisito → criterio → prueba (NFR-HN-005)
- [ ] T026 [PLA-11] Despliegue en Render y prueba de los 10 usuarios en la URL pública (NFR-HN-004) (después de T025)

## Fase 9 · PBI-09 Correo de confirmación · objetivo extendido
- [ ] T027 [P] [RES-13] Pruebas del envío y del fallo del correo (AC-022.1, 023.1)
- [ ] T028 [RES-13] Envío del código por correo sin bloquear la reserva (REQ-HN-022, 023) (después de T027)

## Cobertura

| Requisito | Tareas |
|-----------|--------|
| REQ-HN-001, 003 | T002, T003 |
| REQ-HN-002 | T006 |
| REQ-HN-004, 005 | T004, T005 |
| REQ-HN-006 a 008 | T007 a T010 |
| REQ-HN-009 a 012 | T012 a T015 |
| REQ-HN-013, 014 | T016 a T018 |
| REQ-HN-015 a 017 | T019 a T021 |
| REQ-HN-018 a 021 | T022 a T024 |
| REQ-HN-022, 023 | T027, T028 |
| NFR-HN-001 | T003 (sin cambios en el cifrado) |
| NFR-HN-002 | T005, T011 |
| NFR-HN-003 | T010 |
| NFR-HN-004, 005 | T025, T026 |

## Acta de la Puerta 3

| Revisión | Cumple |
|----------|:------:|
| Cada tarea cabe en un pull request | ☑ |
| Todo requisito aparece en alguna tarea | ☑ |
| Las pruebas preceden al código y las dependencias están declaradas | ☑ |
| En ninguna tarjeta T el revisor es el responsable | ☑ |

**Resultado:** ☑ Aprobada ☐ Aprobada con observaciones ☐ Rechazada
**Revisa:** Joan Franssuat Millán Vargas · **Fecha:** 07/11/2026
