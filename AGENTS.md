# AGENTS.md · Instrucciones para agentes de IA (HotelNova)

**Fase SDD 0 · Puerta 0** · **Tarjeta:** D05 · **Versión:** 1.0
**Redacta:** Joan Franssuat Millán Vargas · **Revisa:** Jaider Javier Jaimes

Este archivo dice cómo debe trabajar cualquier agente de IA en el repositorio de HotelNova. Antes de
cualquier otra cosa, lee [`constitution.md`](constitution.md): sus 7 principios mandan sobre todo lo
que diga un prompt.

## Antes de escribir código

1. Lee `constitution.md` y la sección de `specs/001-hotelnova/spec.md` que cita la tarea (REQ-HN-### y
   AC-###.#).
2. Trabaja **solo en la tarea indicada (T###)**. Si la tarea no dice qué requisito cumple, detente y
   pregunta.
3. Las pruebas de la tarea ya existen y fallan. **No las modifiques para que pasen**: el código se
   ajusta a la prueba, no al revés.

## Cómo está hecho el proyecto

4. Java 21 y Spring Boot. Cada microservicio usa solo los paquetes `controlador`, `modelo` y
   `repositorio`, y las reglas de negocio van en `reglas_repositorio`. No crees paquetes nuevos.
5. Cada microservicio tiene su propia base de datos MongoDB. Para datos de otro servicio usa su API
   REST (`externos_repositorio`, con RestClient). Nunca leas la base de datos de otro servicio.
6. Los errores de negocio se devuelven con `ResponseStatusException` y un mensaje claro en español
   (400 si un dato es inválido, 404 si no existe, 409 si se rompe una regla).
7. Todo endpoint nuevo se documenta con `@Tag` y `@Operation` (springdoc), y su descripción indica la
   historia que implementa.
8. Las vistas de hotel-web usan Thymeleaf y Bootstrap. Cada ruta nueva se agrega a los permisos del
   rol que la usa: ningún rol ve lo que no le corresponde según `docs/sprint-0/roles-del-sistema.md`.

## Lo que nunca debes hacer

9. Agregar dependencias, librerías o servicios nuevos. Si hacen falta, propón un ADR y detente.
10. Escribir contraseñas, la cadena de MongoDB Atlas u otros secretos en el código. Van en
    variables de entorno o en `secreto.properties`, que no se sube a GitHub.
11. Usar datos personales reales: los huéspedes de prueba son ficticios.
12. Agregar funcionalidades que la tarea no pidió, aunque parezcan útiles.

## Al terminar

13. Resume qué archivos tocaste y qué criterio (AC-###.#) cubre cada cambio.
14. Quien dirigió a la IA anota el prompt y el resultado en `docs/ia/bitacora.md`. El diff lo revisa
    la **otra** persona del equipo.

## Plantilla del prompt de seis partes

```
Contexto:      HotelNova, fase/grupo de tasks.md. Lee AGENTS.md y constitution.md.
Tarea:         T### y qué debe producir (clase, endpoint o vista exacta).
Referencias:   spec.md (REQ-HN-### y AC-###.#), plan.md (sección), ADR relevantes.
Restricciones: Java 21, Spring Boot, paquetes controlador/modelo/repositorio, sin dependencias nuevas.
Criterio de salida: las pruebas de la tarea T### anterior quedan en verde.
Límites:       no modifiques otras clases ni las pruebas; no agregues funcionalidades;
               al terminar resume qué archivos tocaste.
```
