# ADR-007 · Toda reserva guarda el canal y quién la tomó

**Estado:** Aceptada en el Planning del R2-Sprint 2 (D14) · **Fecha:** 14/11/2026 · **Cubre:** REQ-HN-013, 014 · RN-07, RN-08

## Contexto
Las reservas se crean desde tres lugares de `hotel-web`: recepción (RES-01), el huésped en línea y, desde
este sprint, el agente de reservas (RES-11). La gerencia quiere saber por dónde llegan (RN-07) y las reglas de
fechas cruzadas y mantenimiento deben ser las mismas para todos (RN-08).

## Decisión
1. Agregar a `Reserva` el enum `Canal` (TELEFONO, CORREO, WHATSAPP, WEB, MOSTRADOR) y el campo `tomadaPor`.
2. `POST /api/reservas` los exige (400 si falta alguno) y luego aplica las mismas validaciones del Release 1.
3. `hotel-web` los llena según quién reserva: recepción → `MOSTRADOR`; huésped en la web → `WEB`;
   agente → el canal que elige (TELEFONO, CORREO o WHATSAPP). `tomadaPor` es el usuario de la sesión.
4. Modificar una reserva conserva su canal y quién la tomó.
5. El agente tiene sus propias páginas (`/agente/**`) sin check-in, check-out, cuentas ni pagos (REQ-HN-014);
   `FiltroSesion` no le da `/recepcion` ni `/caja`.

## Alternativas descartadas
- **Dejar el canal opcional con MOSTRADOR por defecto:** no rompe a ningún cliente viejo, pero permitiría reservas sin
  canal real y la gerencia mediría mal (contradice RN-07).
- **Darle al agente las páginas de recepción ocultando botones:** basta con conocer la ruta para hacer un check-in;
  el permiso tiene que estar en la ruta (ADR-001).

## Consecuencias
- Las reservas del Release 1 no tienen canal: las pantallas muestran "—" o "Sin registrar".
- `hotel-web` y `ms-reservas` deben desplegarse juntos: un `hotel-web` viejo contra un `ms-reservas` nuevo recibe 400
  al reservar.
