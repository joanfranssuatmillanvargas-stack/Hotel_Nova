# Constitución del proyecto HotelNova

**Fase SDD 0 · Puerta 0** · **Tarjeta:** D05 · **Versión:** 1.0
**Redacta:** Joan Franssuat Millán Vargas · **Revisa y ratifica (PO):** Jaider Javier Jaimes

Estos son los principios no negociables de HotelNova. Valen para todo el código, todas las
especificaciones y todo el trabajo hecho con IA. Cada principio dice **qué evita**; un principio
que no evita nada concreto no entra aquí. Un cambio a esta constitución exige un ADR y una nueva
Puerta 0.

## Principios

| # | Principio | Qué evita | Cómo se verifica |
|---|-----------|-----------|------------------|
| 1 | **Ninguna funcionalidad sin criterios de aceptación aprobados.** Todo endpoint, vista o regla nace de un requisito de `spec.md` con su criterio Given/When/Then, aprobado por el PO en su puerta. | Código que nadie pidió y que no hay forma de verificar | Cada pull request cita REQ-HN-### y AC-###.# |
| 2 | **Las pruebas se escriben antes del código.** Primero la prueba que falla por la razón correcta; después la implementación. | Pruebas que se acomodan al código en lugar de a la spec | Historial del repositorio: la prueba llega antes que la clase |
| 3 | **Cada microservicio es dueño de sus datos.** Base de datos propia en MongoDB (database-per-service); para pedir datos de otro servicio se llama a su API REST documentada en OpenAPI, nunca a su base de datos. | Acoplamiento por base de datos compartida y servicios que no se pueden desplegar por separado | Revisión del diff: ninguna conexión a la base de otro servicio |
| 4 | **Simplicidad.** Spring Boot con los paquetes `controlador`, `modelo` y `repositorio`; ninguna dependencia, patrón o servicio nuevo sin un ADR que lo justifique. | Arquitecturas y librerías que nadie pidió, y código que el equipo no sabe explicar | `pom.xml` y estructura de paquetes en la revisión del diff |
| 5 | **Seguridad por defecto y mínimo privilegio.** Cada uno de los 10 roles ve y hace solo lo de su matriz de permisos; las contraseñas se guardan con hash; los secretos (cadena de MongoDB Atlas) van solo en variables de entorno o en `secreto.properties`, que está en `.gitignore`. | Accesos indebidos entre roles y contraseñas publicadas en GitHub | Prueba de acceso por rol; `git grep` sin secretos |
| 6 | **Todo cambio de estado deja rastro y los datos personales se protegen.** Los cambios de estado de habitaciones, reservas y facturas guardan quién los hizo y cuándo. Los datos de huéspedes se tratan según la Ley 1581 de 2012: no se usan datos reales en pruebas ni se escriben en los logs. | Cobros o cambios sin responsable, e incumplimiento legal | Campos de auditoría en el modelo; revisión de logs y datos de prueba |
| 7 | **Todo pull request referencia una tarea.** Ningún cambio llega a `main` sin una tarea T### de `tasks.md` (o un D## para documentos) y la revisión de la otra persona del equipo. | Cambios que no se pueden rastrear a un requisito | Título del PR y matriz de trazabilidad |

## Alcance de la constitución

- Aplica a los 4 microservicios (ms-habitaciones, ms-reservas, ms-huespedes,
  ms-servicios-facturacion) y a la aplicación web hotel-web.
- Las instrucciones concretas para los agentes de IA están en [`AGENTS.md`](AGENTS.md), que
  remite a este archivo.
- Lo que la constitución no cubre se decide en la spec (lo decide el PO) o en un ADR (lo deciden
  los Developers).

## Puerta 0 · Lista de verificación

| # | Pregunta | Sí / No |
|---|----------|:-------:|
| 1 | ¿Hay como máximo siete principios? | ☑ Sí |
| 2 | ¿Cada principio dice qué evita y cómo se verifica? | ☑ Sí |
| 3 | ¿Ningún principio es una preferencia vaga ("buen código", "última versión")? | ☑ Sí |
| 4 | ¿Los principios son coherentes con la arquitectura ya construida (4 microservicios + hotel-web)? | ☑ Sí |
| 5 | ¿`AGENTS.md` remite a esta constitución? | ☑ Sí |
| 6 | ¿El PO ratificó los principios? | ☑ Sí |

## Acta de la Puerta 0

| Campo | Valor |
|-------|-------|
| Fecha | 03/11/2026 |
| Asistentes | Jaider Javier Jaimes (PO), Joan Franssuat Millán Vargas (SM) |
| Resultado | ☑ Aprobada ☐ Aprobada con observaciones ☐ Rechazada |
| Observaciones | |
| Ratifica (PO) | Jaider Javier Jaimes |
