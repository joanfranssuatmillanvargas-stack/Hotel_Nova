# ADR-004 · Inicio provisional para los roles del Sprint 2 y 3

**Estado:** Propuesta para la Puerta 2 · **Fecha:** 07/11/2026 · **Cubre:** REQ-HN-002

## Contexto
El Sprint Goal del R2-Sprint 1 pide que los 10 roles inicien sesión cada uno en su inicio, pero las funciones del
cajero, el agente y el supervisor llegan en el R2-Sprint 2, y las del gerente en el R2-Sprint 3.

## Decisión
Crear desde el Sprint 1 la ruta de inicio de esos 4 roles con una pantalla común (`rol-provisional.html`) que muestra
el nombre del rol, las funciones que va a tener y el sprint en que llegan. Su menú solo tiene ese inicio.

## Alternativa descartada
No crear las rutas hasta su sprint. Esos usuarios no podrían iniciar sesión (el sistema no tendría a dónde llevarlos)
y no se podría demostrar el aislamiento entre roles (REQ-HN-004) con los 10.

## Consecuencias
- Cada pantalla provisional se reemplaza por la real en su sprint, sin cambiar la ruta ni los permisos.
- La Review del Sprint 1 puede mostrar los 10 inicios de sesión.
