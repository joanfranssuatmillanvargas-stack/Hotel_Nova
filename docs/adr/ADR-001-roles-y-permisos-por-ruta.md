# ADR-001 · Roles nuevos en el enum `Rol` y permisos por ruta en `FiltroSesion`

**Estado:** Propuesta para la Puerta 2 · **Fecha:** 07/11/2026 · **Cubre:** REQ-HN-001, 002, 004, 005

## Contexto
`hotel-web` ya maneja 5 roles con un enum `Rol` (cada valor trae su etiqueta y su página de inicio) y un
interceptor, `FiltroSesion`, que exige sesión y deja entrar a cada sección según el rol. El Release 2 suma 5 roles.

## Decisión
Agregar `GERENTE`, `CAJERO`, `MANTENIMIENTO`, `SUPERVISOR_LIMPIEZA` y `AGENTE_RESERVAS` al enum, cada uno con su ruta
de inicio (`/gerencia`, `/caja`, `/mantenimiento`, `/supervision`, `/agente`), y extender la tabla de permisos de
`FiltroSesion` con esas 5 rutas. Cada ruta nueva es exclusiva de su rol, como dice la matriz de permisos.

## Alternativa descartada
Migrar a Spring Security con `@PreAuthorize` por método. Es más estándar, pero cambia el inicio de sesión y las
contraseñas que ya funcionan en producción en medio del release, y no agrega nada que la spec pida.

## Consecuencias
- Un rol nuevo se agrega en dos lugares: el enum y la tabla de permisos. La prueba de `FiltroSesion` falla si se olvida uno.
- La autorización sigue siendo por prefijo de ruta: las páginas de cada rol deben vivir debajo de su prefijo.
