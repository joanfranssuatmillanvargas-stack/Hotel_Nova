# ADR-003 · La reparación es una novedad más (`REPARACION`)

**Estado:** Propuesta para la Puerta 2 · **Fecha:** 07/11/2026 · **Cubre:** REQ-HN-006, 007, 008

## Contexto
`ms-habitaciones` guarda en la colección `novedades` las limpiezas y los daños (tipo `LIMPIEZA` o `DANO`), con la
habitación, quién, cuándo y una descripción. El historial de limpieza del administrador (HAB-14) lee esa colección.

## Decisión
Agregar el tipo `REPARACION` a `TipoNovedad`. Registrar una reparación guarda una novedad de ese tipo con el técnico y
lo que hizo, y pasa la habitación de `MANTENIMIENTO` a `LIMPIEZA`. El daño que se muestra al técnico es la novedad
`DANO` más reciente de la habitación.

## Alternativa descartada
Una colección `reparaciones` aparte. Separaría el historial de cada habitación en dos lugares y obligaría a cambiar
el reporte HAB-14 para unirlos.

## Consecuencias
- El historial de una habitación queda completo en un solo lugar: daño → reparación → limpieza.
- Las pantallas que muestran el tipo de novedad deben reconocer el tipo nuevo (limpieza y reportes del administrador).
