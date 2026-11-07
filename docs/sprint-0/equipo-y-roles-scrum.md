# Equipo Scrum de HotelNova y reparto de roles

**Tarjeta:** D01 · **Responsable:** Joan Franssuat Millán Vargas (SM) · **Revisa:** Jaider Javier Jaimes
**Versión:** 1.0 · Sprint 0 (28/10/2026 – 03/11/2026)

## 1. Integrantes

| Integrante | Correo | Rol Scrum | Como Developer | Microservicios a cargo |
|------------|--------|-----------|----------------|------------------------|
| Jaider Javier Jaimes | jaiderjjaimes@uts.edu.co | **Product Owner** + Developer | Pruebas y calidad: deriva las pruebas desde los criterios, escribe `tasks.md`, matriz de trazabilidad | ms-huespedes, ms-servicios-facturacion |
| Joan Franssuat Millán Vargas | jfmillan@uts.edu.co | **Scrum Master** + Developer | Especificación y plan: redacta spec, plan, ADR y OpenAPI; infraestructura (Docker, Render) | ms-habitaciones, ms-reservas, hotel-web |

Docente: hace de **stakeholder** (gerencia y personal del hotel) en la clarificación y en las
Reviews, como lo propone el taller.

## 2. Por qué dos roles por persona (simplificación del aula)

El taller está pensado para equipos de 6 y dice que con menos de 4 personas desaparecen el revisor y
la rotación de sombreros. Nuestro equipo es de **2 personas y no se puede ampliar** (registrado como
IMP-01). Seguimos la misma lógica que el taller usa para 4 personas: un rol doble declarado como
simplificación del aula.

- **Jaider = Product Owner + Developer.** Decide el orden del backlog, aprueba la spec en sus puertas
  y toma la decisión de release.
- **Joan = Scrum Master + Developer.** Facilita los eventos, registra y escala los impedimentos y
  cuida el tablero, sin decidir el contenido del producto.

**SIMPLIFICACIÓN DEL AULA.** Ambos tienen rol doble y ambos son Developers. Para no perder lo que el
taller protege, adoptamos las cinco reglas de abajo.

## 3. Reglas para que el método no pierda su sentido con 2 personas

1. **Revisión cruzada siempre.** El revisor de una tarjeta es la otra persona. La columna
   *Control revisor* del Excel marca ERROR si responsable y revisor coinciden.
2. **El PO no escribe lo que aprueba.** Joan redacta spec, plan y ADR; Jaider (PO) los revisa y
   aprueba en las Puertas 1 y 2. Jaider escribe `tasks.md` y las pruebas, y Joan las revisa.
3. **Nadie aprueba el diff de la IA que dirigió.** Si Joan dirigió a la IA en una tarea, Jaider
   revisa el diff, y al revés.
4. **El SM no decide por el PO.** Cuando aparece una duda de negocio, Joan registra el impedimento y
   Jaider decide; la decisión queda escrita en la spec.
5. **Límites WIP a escala.** Máximo 2 tarjetas *En progreso* (1 por persona) y 2 *En revisión / Pruebas*.

## 4. Sombreros rotativos

Con dos personas los sombreros se alternan por sprint. El revisor del diff de la IA no rota: es
siempre la persona que **no** dirigió a la IA en esa tarjeta.

| Sombrero | R2-Sprint 1 | R2-Sprint 2 | R2-Sprint 3 | Qué hace |
|----------|----------|----------|----------|----------|
| Facilitador del Daily | Joan | Jaider | Joan | Abre el Daily, cuida los 15 minutos y actualiza el tablero |
| Guardián de la DoD | Jaider | Joan | Jaider | Verifica el checklist antes de que una tarjeta pase a Terminado |
| Revisor del diff de la IA | El otro | El otro | El otro | Revisa el diff completo; nunca quien dirigió a la IA |

## 5. Qué decide cada uno y qué no le corresponde

| Rol | Qué decide | Qué no le corresponde |
|-----|-----------|-----------------------|
| Product Owner (Jaider) | Orden del backlog, aceptación de la spec, decisión de release | Decir cómo se programa; aprobar documentos que él mismo redactó |
| Scrum Master (Joan) | Cómo se facilitan los eventos; registra y escala impedimentos | Decidir el contenido del producto o repartir tarjetas |
| Developers (ambos) | Cuánto cabe en el sprint y cómo construirlo | Ordenar el backlog; aprobar su propio diff |

## 6. Eventos de Scrum (sprints de miércoles a martes)

| Evento | Cuándo | Duración | Facilita |
|--------|--------|----------|----------|
| Sprint Planning | Miércoles (día 1) | 1 h | Joan |
| Daily Scrum | Jueves a martes, 4:00 p. m. (inicio de la jornada) | 15 min | Sombrero del sprint |
| Sprint Review | Martes (día 5) | 45 min | Joan; Jaider presenta |
| Sprint Retrospective | Martes (día 5), después de la Review | 30 min | Joan |
| Refinamiento | Lunes (día 4) | 30 min | Jaider |

**Canal de impedimentos:** grupo de WhatsApp del equipo. Se avisa apenas aparece el impedimento, sin
esperar al Daily.
