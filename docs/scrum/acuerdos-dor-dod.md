# Acuerdos del equipo, Definition of Ready y Definition of Done (versión 1)

**Tarjeta:** D04 · **Responsable:** Joan Franssuat Millán Vargas (SM) · **Revisa:** Jaider Javier Jaimes
**Versión:** 1.0 · Sprint 0 · Se revisan en cada Retrospective.

## 1. Acuerdos del equipo (working agreement)

- **Horario:** jornada de trabajo del proyecto de 4 p. m. a 8 p. m. El Daily es a las 4:00 p. m. por
  videollamada o en la universidad. Las reuniones empiezan y terminan a tiempo.
- **Comunicación:** las dudas de negocio se le preguntan al PO (Jaider) por el grupo del equipo, no por
  privado. Lo que se acuerda se escribe en la spec.
- **Código:** nada llega a `main` sin pull request, tarea asociada y revisión de la otra persona.
  Pruebas antes que código.
- **IA:** se usa con el prompt de seis partes de `AGENTS.md`, nunca con datos reales. Se revisa el
  diff completo y se anota en `docs/ia/bitacora.md`.
- **Impedimentos:** se le avisan al SM (Joan) apenas aparecen, sin esperar al Daily. Si el
  impedimento es de Joan, se lo avisa a Jaider.
- **Tablero:** se actualiza antes del Daily. Se respetan los límites WIP (2 en progreso, 2 en revisión / pruebas).
  Nadie mueve su propia tarjeta a Terminado.
- **Respeto:** se critica el trabajo, no a la persona. Las discrepancias técnicas se resuelven con un
  ADR; las de negocio, con la decisión del PO.

## 2. Definition of Ready (DoR)

Un ítem puede entrar al Sprint Planning cuando cumple las seis condiciones.

| # | Condición | Verifica |
|---|-----------|----------|
| 1 | Tiene una intención clara y su valor ("Como … quiero … para …") | PO (Jaider) |
| 2 | Tiene al menos 2 criterios de aceptación gruesos, uno de ellos de borde o de error | PO y Developers |
| 3 | Cabe en un sprint y está estimado en puntos | Developers |
| 4 | Sus dependencias están identificadas y ninguna depende de alguien de fuera del equipo | SM (Joan) |
| 5 | Se sabe a qué stakeholder preguntar por las reglas de negocio (el docente hace de gerencia del hotel) | PO |
| 6 | No exige la spec detallada: esa se escribe al tomar el ítem dentro del sprint | Todos |

## 3. Definition of Done (DoD)

Un ítem está terminado cuando cumple las doce condiciones.

| # | Condición | Cómo se verifica |
|---|-----------|------------------|
| 1 | La spec del ítem fue aprobada por el PO en su puerta y no tiene marcadores [NECESITA ACLARACIÓN] | Acta de la puerta |
| 2 | Cada criterio de aceptación tiene al menos una prueba automatizada, incluidos los casos de borde | Matriz de trazabilidad |
| 3 | Las pruebas se escribieron antes del código y pasan (`mvn test`) | Historial del repositorio |
| 4 | El código llegó a `main` por un pull request que cita la tarea (T###) y fue revisado por la otra persona | Pull request |
| 5 | El diff no incluye funcionalidad ni dependencias que ningún requisito pidió (o están en un ADR) | Revisión del diff |
| 6 | Respeta los 3 paquetes (controlador, modelo, repositorio) y la base de datos propia del microservicio | Revisión del diff |
| 7 | Cada ruta y endpoint nuevo solo es accesible para los roles que indica la matriz de permisos | Prueba de acceso por rol |
| 8 | Los cambios de estado guardan quién y cuándo; no hay datos personales reales ni secretos en el repositorio | Revisión del diff y `git grep` |
| 9 | El endpoint está documentado en Swagger (OpenAPI) y la descripción cita la historia | `/swagger-ui.html` |
| 10 | La matriz de trazabilidad incluye el ítem (requisito, criterio, prueba) | `docs/trazabilidad.md` |
| 11 | El uso de IA quedó anotado en la bitácora | `docs/ia/bitacora.md` |
| 12 | Está desplegado en Render y se puede demostrar en la Review con su rol de prueba | URL pública |
