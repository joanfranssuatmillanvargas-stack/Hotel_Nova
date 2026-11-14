# ADR-006 · El pago guarda el cajero y el cierre de caja se calcula al consultarlo

**Estado:** Aceptada en el Planning del R2-Sprint 2 (D14) · **Fecha:** 14/11/2026 · **Cubre:** REQ-HN-009 a 012

## Contexto
Desde el Release 1 la recepcionista registra el pago de la factura (SER-08) con `POST /api/facturas/{id}/pago?medio=`
y el pago queda embebido en la factura (`Pago`: medio, fecha, valor). La spec pide que solo el cajero cobre
(RN-05, REQ-HN-011), que quede quién cobró (REQ-HN-009) y un cierre de caja del día por medio de pago (REQ-HN-012).

## Decisión
1. `Pago` guarda también `cajero`. El endpoint de pago conserva sus parámetros en la URL y agrega `cajero`
   (obligatorio, 400 si falta). Una factura PAGADA sigue respondiendo 409.
2. `GET /api/facturas/cierre?fecha=&cajero=` **calcula** el cierre con los pagos de ese día y de ese cajero
   (fecha del pago, no de la factura). No se guarda: un día sin pagos devuelve todo en 0.
3. Solo la página `/caja` registra pagos. Se quitó el formulario de pago y la ruta `/recepcion/estadia/{id}/pago`
   de recepción; la estadía muestra "El pago lo registra el cajero".

## Alternativas descartadas
- **Guardar cada cierre en una colección `cierres`:** permitiría "cerrar" la caja y no tocarla más, pero el
  alcance del sprint no pide bloquear pagos después del cierre y duplicaría datos que ya están en las facturas.
- **Un cierre de todo el hotel sin importar el cajero:** no sirve para cuadrar el dinero de cada caja.

## Consecuencias
- Los pagos del Release 1 no tienen cajero y no aparecen en ningún cierre; siguen contando en el reporte de
  ingresos del administrador (SER-10).
- Si un día hay dos cajeros, cada uno genera su propio cierre.
