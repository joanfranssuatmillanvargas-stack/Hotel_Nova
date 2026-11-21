# ADR-010 · Identidad visual de hotel-web

**Estado:** Aceptada por el PO el 21/11/2026 (R2-Sprint 3, T029) · **Cubre:** NFR-HN-003 (uso en el celular) · todas las pantallas

## Contexto
Las pantallas usaban Bootstrap casi sin cambios: azul por defecto, tarjetas planas y una barra azul. Al revisar el
incremento, el equipo vio el diseño "muy simple" para la Review final. El cambio no debe tocar rutas, formularios ni
reglas, porque las pruebas de aceptación (P021 a P034) se hacen sobre esas pantallas.

## Decisión
1. Toda la identidad vive en `static/css/hotel.css` y en `fragmentos.html` (cabeza, menú y scripts). Las páginas solo
   cambian donde muestran el número de habitación (clase `placa`) y en el inicio de sesión (nueva distribución con los
   mismos campos `usuario` y `clave`).
2. Paleta de hotel de noche: tinta `#16323A` para la estructura, verde lobby `#1F6F6B` para las acciones y latón
   `#B88A3E` solo como acento (la llave de la marca, la línea bajo el menú y la página activa).
3. Tipografía: Figtree para el texto y Barlow Condensed para la marca, las cifras del tablero y los números de
   habitación (como las placas de las puertas). Se cargan desde Google Fonts igual que Bootstrap se carga desde su CDN;
   si no cargan, el navegador usa la letra del sistema y nada se rompe.
4. El menú marca la página activa con un script de 10 líneas en el fragmento `scripts`, y muestra el usuario con su rol.

## Alternativas descartadas
- **Cambiar a otra librería de componentes o a un tema de pago:** es una dependencia nueva (AGENTS.md, regla 9) y
  obligaría a reescribir las 30 plantillas a dos días de la Review.
- **Rediseñar página por página:** más riesgo de romper formularios que ya pasaron sus pruebas en Render.

## Consecuencias
- Las pruebas unitarias no cambian (61 de 61 en hotel-web). P032 a P034 se ejecutan ya con el diseño nuevo.
- Sin internet hacia Google Fonts, la aplicación se ve con la letra del sistema.
