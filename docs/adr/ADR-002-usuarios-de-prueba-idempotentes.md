# ADR-002 · Usuarios de prueba creados al arrancar, solo si no existen

**Estado:** Propuesta para la Puerta 2 · **Fecha:** 07/11/2026 · **Cubre:** REQ-HN-003

## Contexto
En el Release 1, `cuentas_repositorio` crea los usuarios de prueba solo si la colección `usuarios` está vacía. En
Render la colección ya tiene usuarios, así que los 5 usuarios nuevos nunca se crearían.

## Decisión
Al arrancar, recorrer la lista de los 9 usuarios del personal (`admin`, `recepcion`, `servicios`, `limpieza`,
`gerente`, `cajero`, `mantenimiento`, `supervisor`, `agente`) y crear cada uno solo si su usuario no existe. Nunca se
modifica un usuario existente: ni su contraseña ni si está activo.

## Alternativa descartada
Crearlos a mano en MongoDB Atlas o desde la pantalla del administrador. Depende de que alguien se acuerde en cada
ambiente (local, Render) y no queda en el código.

## Consecuencias
- Es seguro arrancar la aplicación muchas veces: no duplica ni pisa usuarios.
- Si alguien borra un usuario de prueba, vuelve a aparecer en el siguiente arranque con su contraseña de prueba.
  Antes de un uso real del sistema hay que cambiar esas contraseñas (queda anotado en LEEME.txt).
