# Tableros Kanban de HotelNova

**Tarjeta:** D06 · **Responsable:** Joan Franssuat Millán Vargas (SM) · **Revisa:** Jaider Javier Jaimes
**Versión:** 1.1 · Sprint 0 (actualizada el 02/11/2026 con los tableros ya montados en Trello)

Los tableros viven en **Trello, uno por sprint**, en el espacio de trabajo del equipo. Hay 8 tableros:
los 4 del **Release 1** (segundo corte, ya terminado) y los 4 del **Release 2** (tercer corte). Como
recomienda el taller (Parte VI), Trello es el tablero del día a día y el Excel (`HotelNova_Scrum.xlsx`,
hoja Tareas_SDD) es la fuente de verdad: cuando una tarjeta cambia de lista en Trello, se actualiza su
columna *Lista* en el Excel. La hoja **Tablero** del Excel guarda el enlace de cada tablero, cuenta las
tarjetas por lista y controla el WIP.

## 1. Los 8 tableros

| Tablero | Fechas | Contenido | Enlace |
|---------|--------|-----------|--------|
| HotelNova · R1-Sprint 0 | 05/10 – 11/10/2026 | 6 tarjetas de preparación, en Terminado | https://trello.com/b/04UJhbkO/hotelnova-r1-sprint-0 |
| HotelNova · R1-Sprint 1 | 12/10 – 18/10/2026 | 20 historias de usuario, en Terminado | https://trello.com/b/wItoVhUh/hotelnova-r1-sprint-1 |
| HotelNova · R1-Sprint 2 | 19/10 – 25/10/2026 | 15 historias de usuario, en Terminado | https://trello.com/b/e34YmtPS/hotelnova-r1-sprint-2 |
| HotelNova · R1-Sprint 3 | 26/10 – 01/11/2026 | 17 historias de usuario, en Terminado | https://trello.com/b/Qxbjcasl/hotelnova-r1-sprint-3 |
| HotelNova · R2-Sprint 0 | 28/10 – 03/11/2026 | D01 a D06, en Terminado | https://trello.com/b/ovSVuYcj/hotelnova-r2-sprint-0 |
| HotelNova · R2-Sprint 1 | 04/11 – 10/11/2026 | PBI-01 y 4 historias en Historias del Sprint; D07 a D13 en Por hacer | https://trello.com/b/mIjYxcN6/hotelnova-r2-sprint-1 |
| HotelNova · R2-Sprint 2 | 11/11 – 17/11/2026 | 6 historias en Historias del Sprint | https://trello.com/b/RaeavQ3Y/hotelnova-r2-sprint-2 |
| HotelNova · R2-Sprint 3 | 18/11 – 24/11/2026 | 3 historias + RES-13 (objetivo extendido) en Historias del Sprint | https://trello.com/b/Gehl10Hu/hotelnova-r2-sprint-3 |

Los tableros del Release 1 se armaron después, como evidencia de lo que ya estaba terminado: las 52
historias de usuario de la hoja *2. Historias de usuario* del Excel, repartidas en su sprint.

Cada tablero tiene arriba, en *Historias del Sprint*, una tarjeta **SPRINT GOAL** con el objetivo del
sprint, sus fechas y el compromiso en puntos.

## 2. Listas (en este orden, iguales en los 8 tableros)

| Lista | Qué significa | Límite WIP | Entra cuando… | La mueve |
|-------|---------------|:----------:|---------------|----------|
| **Historias del Sprint (Stories)** | Las historias de usuario y PBI que el equipo se comprometió a hacer en el sprint, con el Sprint Goal arriba | Lo que cabe en el sprint | Se eligen en el Sprint Planning | El PO, en el Planning |
| **Por hacer (To Do)** | Tarjeta T o D creada con responsable, revisor y checklist | Sin límite | Se crea en el Planning o al aprobar la Puerta 3 | Developers |
| **En progreso (In Progress)** | Alguien trabaja en ella ahora | 2 en total (1 por persona) | Su responsable la toma (pull) y sus dependencias están en revisión o terminadas | El responsable |
| **En revisión / Pruebas (Testing)** | PR o documento abierto, esperando a la otra persona; se corren las pruebas | 2 en total | El responsable completó su parte del checklist | El responsable |
| **Terminado (Done)** | Cumple la Definition of Done | — | El revisor y el guardián de la DoD confirman el checklist | El revisor |

En los tableros del Release 1 las historias están directamente en **Terminado (Done)**, porque ese
release ya se entregó.

**Tarjetas bloqueadas.** No hay una lista "Bloqueado": la tarjeta se queda en la lista donde estaba, se
le pone la etiqueta roja **Impedimento** y se escribe en un comentario el IMP-## que la bloquea (hoja
Impedimentos del Excel). El SM lo escala.

El Facilitador del Daily revisa cada día que *En progreso* y *En revisión / Pruebas* no pasen de 2.

## 3. Etiquetas

| Etiqueta | Color | Para qué |
|----------|-------|----------|
| Tarea | Azul | Línea de tasks.md (T###) |
| SDD | Verde | Documento de especificación con su puerta (constitución, spec, plan, tareas, delta) |
| Scrum | Morado | Documento o evento de Scrum (acuerdos, planning, review, retro) |
| Calidad | Amarillo | Pruebas, trazabilidad, Ley 1581, revisión de seguridad |
| Entrega | Naranja | Despliegue en Render, notas de versión, decisión de release |
| Impedimento | Rojo | Tarjeta bloqueada por un IMP-## |
| CR-001 | Negro | Todo lo de una solicitud de cambio (si aparece) |
| [P] | Celeste | Tarea que puede hacerse en paralelo |
| No planificada | Rosa | Trabajo que no estaba en el Planning |

En las tarjetas ya creadas, la etiqueta va escrita en la descripción. Para verla de color, en cada
tablero se crean desde **Menú → Etiquetas** y se asignan a la tarjeta.

## 4. Formato de las tarjetas

**Historia de usuario** (lista Historias del Sprint):

```
Título:       HAB-16 — <lo que quiere el usuario> (2 pts)
Descripción:  Como <rol>, quiero <…>, para que <…>.
              Criterios de aceptación · Microservicio · Técnica de división
              Prioridad · Puntos · Responsable · PBI
```

**Tarea o documento** (listas Por hacer a Terminado):

```
Título:       T### · Título de la tarea          (o D## · Título del documento)
Etiquetas:    Tarea | SDD | Scrum | Calidad | Entrega | [P] | CR-001
Responsable:  ____________   Revisa: ____________ (la otra persona)
Entrega:      aaaa-mm-dd     Horas: ___ (ilustrativas)
Depende de:   T###, T###     PBI: PBI-##
Trazabilidad: REQ-HN-### · AC-###.# · RN-## · ADR-###
Enlaces:      spec.md · plan.md · pull request o documento
```

El título de las tarjetas T y D es la columna **Línea Trello** de la hoja Tareas_SDD, y la fecha de
vencimiento es su columna *Fecha entrega*.

| Checklist de tarjetas T (tasks.md) | Checklist de tarjetas D (documentos y eventos) |
|------------------------------------|------------------------------------------------|
| ☐ Pruebas escritas antes del código y en verde | ☐ Documento versionado en el repositorio (ruta en la tarjeta) |
| ☐ El PR cita la T### y lleva el prompt de seis partes | ☐ Lo revisó la persona indicada en "Revisa" |
| ☐ El diff lo revisó la otra persona (no quien dirigió a la IA) | ☐ La puerta se aprobó y el acta quedó guardada (si aplica) |
| ☐ El diff no trae funcionalidad ni dependencias que nadie pidió | ☐ No quedan marcadores [NECESITA ACLARACIÓN] |
| ☐ Ruta y endpoint solo para los roles de la matriz de permisos | ☐ El enlace al documento está en la tarjeta |
| ☐ Bitácora de IA actualizada | ☐ Las decisiones están escritas en el documento |
| ☐ Matriz de trazabilidad actualizada | |
| ☐ Desplegado en Render (cuando aplica) | |

## 5. Las ocho reglas del tablero

1. Un responsable por tarjeta. El revisor es siempre la otra persona.
2. Nadie mueve su propia tarjeta a Terminado: lo hace el revisor cuando el checklist está completo.
3. Pull, no push: cada uno toma la siguiente tarjeta cuando termina.
4. Se respetan los límites WIP: con la lista llena, primero se termina algo.
5. Una tarjeta bloqueada lleva la etiqueta Impedimento y su IMP: se registra en menos de una hora y el SM lo escala.
6. El tablero se actualiza antes del Daily.
7. Los puntos del burndown se descuentan cuando todas las tarjetas de un PBI están en Terminado; entonces la historia también pasa a Terminado.
8. El alcance del sprint no crece a escondidas: el trabajo nuevo va al backlog y decide el PO.

## 6. Contenido de los tableros del Release 2

### HotelNova · R2-Sprint 0 (28/10 – 03/11) · todas en Terminado a la entrega

| ID | Tarjeta | Responsable | Revisa |
|----|---------|-------------|--------|
| D01 | Equipo, roles Scrum y sombreros | Joan | Jaider |
| D02 | Roles del sistema (10) y matriz de permisos | Jaider | Joan |
| D03 | Product Goal y Product Backlog | Jaider | Joan |
| D04 | Working agreement, DoR y DoD v1 | Joan | Jaider |
| D05 | Constitución y AGENTS.md (Puerta 0) | Joan | Jaider (ratifica como PO) |
| D06 | Tableros Kanban y plantilla Excel | Joan | Jaider |

### HotelNova · R2-Sprint 1 (04/11 – 10/11)

**Historias del Sprint:** PBI-01 (spec, plan y tareas), PLA-08, PLA-09 (PBI-02) y HAB-16, HAB-17 (PBI-03).

**Por hacer:**

| ID | Tarjeta | Responsable | Revisa | Día |
|----|---------|-------------|--------|-----|
| D07 | Sprint Planning S1 | Joan | Equipo | Mié 04 |
| D08 | Spec v0.1 (contexto, 10 roles, HU, RN) | Joan | Jaider | Mié 04 |
| D09 | Clarificación y spec v0.2 | Jaider | Joan | Jue 05 |
| D10 | Spec v1.0 y Puerta 1 | Joan | Jaider (aprueba) | Jue 05 – Vie 06 |
| D11 | plan.md, ADR y openapi.yaml (Puerta 2) | Joan | Jaider | Vie 06 – Lun 09 |
| D12 | tasks.md (Puerta 3) | Jaider | Joan | Lun 09 |
| D13 | Review, Retro y cierre documental S1 | Joan | Equipo | Mar 10 |

Las tarjetas T del Sprint 1 (PBI-02 y PBI-03) se crean en *Por hacer* el lunes 09/11, al aprobar `tasks.md`.

### HotelNova · R2-Sprint 2 (11/11 – 17/11)

**Historias del Sprint:** SER-11, SER-12 (PBI-04), RES-11, RES-12 (PBI-05) y HAB-18, HAB-19 (PBI-06).
Las tarjetas T y D se crean en el Sprint Planning del miércoles 11/11.

### HotelNova · R2-Sprint 3 (18/11 – 24/11)

**Historias del Sprint:** PLA-10, HAB-20 (PBI-07) y PLA-11 (PBI-08). RES-13 (PBI-09) está marcada como
*objetivo extendido*: solo se inicia si sobra capacidad. Las tarjetas T y D se crean en el Sprint
Planning del miércoles 18/11.

## 7. Miembros y uso diario

1. El espacio de trabajo es de Sergio Meneses. Él invita a Joan (`jfmillan@uts.edu.co`) y a Jaider
   (`jaiderjjaimes@uts.edu.co`) desde **Miembros → Invitar a miembros del espacio de trabajo**.
2. Al aceptar, cada uno se pone como **miembro** de sus tarjetas (el responsable ya está escrito en la descripción).
3. Cuando una tarjeta cambia de lista en Trello, se actualiza su columna *Lista* en la hoja Tareas_SDD.
4. Al cerrar cada sprint se guarda una captura del tablero (la piden como evidencia) y se marca "Sí" en
   la hoja **Tablero** del Excel.
