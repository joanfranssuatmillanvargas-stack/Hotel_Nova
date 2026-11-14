# ADR-005 · La asignación de limpieza es un dato de la habitación

**Estado:** Aceptada en el Planning del R2-Sprint 2 (D14) · **Fecha:** 14/11/2026 · **Cubre:** REQ-HN-015, 016, 017

## Contexto
El supervisor de limpieza reparte las habitaciones en `LIMPIEZA` entre las camareras (HAB-18) y cada camarera debe
ver solo las suyas (HAB-19). Las habitaciones viven en `ms-habitaciones`; los usuarios y sus roles viven en
`hotel-web` (`usuariosdb`). Una habitación en `LIMPIEZA` la limpia una sola camarera a la vez.

## Decisión
1. Agregar a `Habitacion` los campos `asignadaA`, `asignadaPor` y `asignadaEn`. `POST /api/habitaciones/{id}/asignacion`
   los llena; reasignar los reemplaza, así que la habitación sale de la lista de la camarera anterior.
2. Solo se asigna una habitación en `LIMPIEZA` (409 en otro estado). Cuando la habitación sale de `LIMPIEZA`
   (queda limpia o pasa a mantenimiento), `cambiarEstado` borra la asignación.
3. `GET /api/habitaciones/limpieza?camarera=` devuelve solo las de esa camarera; sin el parámetro, todas (supervisor).
4. Que la persona elegida sea un usuario **activo con rol LIMPIEZA** lo valida `hotel-web`
   (`cuentas_repositorio.camarera`) antes de llamar al microservicio.

## Alternativas descartadas
- **Una colección `asignaciones` aparte:** guardaría el historial de asignaciones, pero obliga a cruzar dos colecciones
  para la lista de cada camarera y a cerrar asignaciones viejas a mano. El historial de quién limpió ya está en las
  novedades (HAB-14).
- **Que `ms-habitaciones` consulte los usuarios de `hotel-web`:** los usuarios no tienen API REST y la constitución
  prohíbe leer la base de otro servicio (AGENTS.md, regla 5).

## Consecuencias
- No se guarda el historial de reasignaciones; si la gerencia lo pide, entra al backlog como cambio (CR).
- Desde este sprint, el usuario `limpieza` ve solo lo que le asignen. Para la demo hay que asignarle habitaciones
  desde `supervisor` (o crear camareras como `ana` y `luz` en Usuarios).
