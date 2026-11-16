# Registro de impedimentos

Lo lleva el Scrum Master (Joan Franssuat Millán Vargas). La misma información está en la hoja
**Impedimentos** del Excel.

## IMP-01 · El equipo tiene 2 personas y el taller pide mínimo 4

| Campo | Valor |
|-------|-------|
| Fecha | 31/10/2026 (Sprint 0) |
| Registra | Joan Franssuat Millán Vargas (SM) |
| Qué pasó | El taller Scrum + SDD dice que con menos de 4 personas desaparecen el revisor distinto del responsable y la rotación de sombreros. El equipo es de 2 integrantes y no se puede ampliar. |
| Impacto | Todas las tarjetas. Riesgo para el Sprint Goal: **medio**, por la sobrecarga y la revisión cruzada. |
| Dueño | Joan Franssuat Millán Vargas |
| Escalado a | Docente del curso |
| Alternativa | Roles dobles declarados como simplificación del aula: PO + Developer y SM + Developer. Revisión cruzada obligatoria (el revisor es siempre el otro), sombreros alternos por sprint y límites WIP de 2. Ver `docs/sprint-0/equipo-y-roles-scrum.md`. |
| Fecha límite | 03/11/2026 (entrega del Sprint 0) |
| Estado | **Mitigado** |
| Cierre | Se cierra cuando el docente acepte la adaptación en la revisión del Sprint 0. |

## IMP-02 · La especificación del R2-Sprint 1 empezó tres días tarde

| Campo | Valor |
|-------|-------|
| Fecha | 07/11/2026 (R2-Sprint 1, después del día 3) |
| Registra | Joan Franssuat Millán Vargas (SM) |
| Qué pasó | Las tarjetas D07 a D10 (Planning, spec v0.1, clarificación y spec v1.0) estaban para el 04, 05 y 06/11 y no se movieron en esos días. Causa: disponibilidad de tiempo del equipo: entre semana casi no queda tiempo libre y el trabajo arranca casi siempre el sábado, que es el día con más tiempo. |
| Impacto | D07 a D12 y las tareas T001 a T011. Riesgo para el Sprint Goal: **alto**, porque las Puertas 1 a 3 y la construcción quedan para el lunes 09 y el martes 10. |
| Dueño | Joan Franssuat Millán Vargas |
| Escalado a | Equipo (se decide sin escalar al docente) |
| Alternativa | El sábado 07/11 se escribieron la spec, el plan, los ADR, el contrato y tasks.md, se pasaron las Puertas 1, 2 y 3 y se construyó el código con sus pruebas (32 de 32 en verde). D07 a D12 y T001 a T010 quedaron en Terminado ese mismo día. |
| Fecha límite | 09/11/2026 (Puerta 3) |
| Estado | **Cerrado** (07/11/2026) |
| Cierre | Las tres puertas quedaron aprobadas el 07/11/2026. La causa la escribió el equipo el 14/11; la acción para que no se repita se acuerda en la Retro. |

## IMP-03 · El R2-Sprint 2 también empezó después del día 3

| Campo | Valor |
|-------|-------|
| Fecha | 14/11/2026 (R2-Sprint 2, sábado después del día 3) |
| Registra | Joan Franssuat Millán Vargas (SM) |
| Qué pasó | Del miércoles 11 al viernes 13/11 ninguna tarjeta del R2-Sprint 2 se movió: el Planning (D14) y las tarjetas T012 a T021 se hicieron el sábado 14/11. Es la misma situación de IMP-02, con la misma causa: disponibilidad de tiempo del equipo: entre semana casi no queda tiempo libre y el trabajo arranca casi siempre el sábado, que es el día con más tiempo. |
| Impacto | D14 y T012 a T021 (13 puntos). Riesgo para el Sprint Goal: **medio**, porque el lunes 16/11 es festivo y la Review es el martes 17/11. |
| Dueño | Joan Franssuat Millán Vargas |
| Escalado a | Equipo (se decide sin escalar al docente) |
| Alternativa | El sábado 14/11 se hicieron el Planning, la revisión del plan (ADR-005 a 007, OpenAPI v1.1), el código y las pruebas (71 de 71 en verde). Quedan para antes de la Review las pruebas P025 a P031 en Render. |
| Fecha límite | 17/11/2026 (Review del R2-Sprint 2) |
| Estado | **Abierto** |
| Cierre | Se cierra cuando P025 a P031 pasen en Render y la Retro acuerde una acción para que el R2-Sprint 3 arranque el miércoles 18/11. Que se repita después de IMP-02 es tema obligatorio de la Retro. |
