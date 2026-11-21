# ADR-008 · Las temporadas esperan la aprobación del gerente

**Estado:** Aceptada en el Planning del R2-Sprint 3 (D17) · **Fecha:** 18/11/2026 · **Cubre:** REQ-HN-020, 021 · HAB-20

## Contexto
Desde el Release 1 el administrador crea temporadas (HAB-06) y la tarifa de la noche cambia apenas las guarda.
La gerencia quiere que ninguna tarifa cambie sin su autorización (HAB-20). En la base de Render ya hay temporadas
creadas en el Release 1 que hoy se están cobrando.

## Decisión
1. Agregar a `Temporada` el enum `EstadoTemporada` (PENDIENTE, APROBADA, RECHAZADA) y los campos `revisadaPor` y
   `revisadaEn`.
2. Crear o editar una temporada (`POST` y `PUT /api/temporadas`) la deja PENDIENTE y borra la revisión anterior:
   un cambio de tarifa siempre vuelve a pasar por el gerente.
3. `POST /api/temporadas/{id}/aprobacion?decision=APROBADA|RECHAZADA&gerente=...` solo decide temporadas PENDIENTES
   (409 si ya se decidió; 400 si falta el gerente o la decisión no es válida).
4. `tarifaNoche` solo usa las temporadas vigentes: APROBADAS o sin estado.
5. Las temporadas **sin estado** (guardadas antes del R2-Sprint 3) se tratan como aprobadas: ya se estaban cobrando.
6. Una temporada RECHAZADA no cuenta para el cruce de fechas, así que el administrador puede crear otra corregida
   en las mismas fechas.
7. En `hotel-web` el gerente aprueba o rechaza en `/gerencia/temporadas`; el administrador ve el estado en su tabla
   de temporadas. `FiltroSesion` ya daba `/gerencia` solo al GERENTE y `/admin` solo al ADMINISTRADOR (sin cambios).

## Alternativas descartadas
- **Dejar PENDIENTES las temporadas viejas (sin estado):** al desplegar, las tarifas que ya se cobran volverían a la
  tarifa base de un día para otro sin que nadie lo decidiera.
- **Guardar la aprobación en `hotel-web`:** la regla de qué tarifa aplica vive en `ms-habitaciones` (ADR-003 y el
  cálculo de `valor`); si la aprobación quedara en la web, una llamada directa a la API saltaría la regla.
- **Mantener una tarifa "propuesta" aparte de la vigente al editar:** cubre el caso de editar una temporada ya
  aprobada sin perder la tarifa anterior, pero duplica campos y pantallas para un caso que hoy no pidió nadie.

## Consecuencias
- Mientras una temporada editada está PENDIENTE, sus noches se cobran a la tarifa base (no a la tarifa aprobada antes
  de editarla). Se le avisa al administrador al guardar.
- `hotel-web` y `ms-habitaciones` se despliegan juntos: un `hotel-web` viejo no muestra el estado ni deja aprobar.
- "Cargar datos de ejemplo" crea temporadas PENDIENTES: sirven para la demo del gerente.
