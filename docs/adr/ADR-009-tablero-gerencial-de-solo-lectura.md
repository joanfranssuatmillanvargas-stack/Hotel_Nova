# ADR-009 · El tablero gerencial junta los reportes que ya existen y no guarda nada

**Estado:** Aceptada en el Planning del R2-Sprint 3 (D17) · **Fecha:** 18/11/2026 · **Cubre:** REQ-HN-018, 019 · PLA-10

## Contexto
El gerente necesita ver la ocupación del día, la tarifa promedio por noche vendida y los ingresos del periodo por
categoría (PLA-10) consultando los 4 microservicios por REST, sin poder crear, editar ni cobrar. Casi todo ya existe
en los reportes del Release 1 que usa el administrador.

## Decisión
1. Nueva clase `tablero_repositorio` en `hotel-web` (paquete `repositorio`) que llama, sin guardar nada:
   `GET /api/habitaciones/ocupacion` (ocupación de hoy), `GET /api/reservas/reporte` (reservas del periodo),
   `GET /api/facturas/reporte` (ingresos por categoría) y `GET /api/huespedes` (huéspedes registrados). Además cuenta
   las temporadas PENDIENTES (`GET /api/temporadas?estado=PENDIENTE`) para avisar al gerente (HAB-20).
2. **Tarifa promedio por noche vendida** = valor del alojamiento de las reservas no canceladas del periodo / noches de
   esas reservas, redondeado al peso. Se calcula en `ms-reservas` (el dueño de las noches) y se agrega al reporte que
   ya existía (`noches` y `tarifaPromedio`). Sin noches vendidas, 0.
3. **Ingresos por categoría** = alojamiento facturado más cada categoría de servicio del reporte de facturas (sin IVA);
   el IVA y el total se muestran aparte.
4. El periodo por defecto es el mes actual; si "hasta" es anterior a "desde", el tablero avisa y muestra el mes.
5. `gerencia_web` reemplaza el inicio provisional de `/gerencia` (ADR-004): con esto se retira `roles_nuevos_web` y
   `rol-provisional.html`, porque los 10 roles ya tienen sus pantallas reales.

## Alternativas descartadas
- **Un microservicio de reportes nuevo:** es otro servicio que desplegar en Render y otra dependencia (AGENTS.md,
  regla 9), para algo que se resuelve combinando 4 llamadas.
- **Calcular la tarifa promedio en `hotel-web` pidiendo todas las reservas:** trae al navegador datos personales de
  huéspedes que el gerente no necesita (NFR-HN-002) y repite la regla de qué reservas cuentan.
- **Calcular la tarifa promedio con las facturas:** solo cuenta estadías ya cerradas; el gerente quiere ver también lo
  vendido que todavía no se factura.

## Consecuencias
- Si un microservicio está dormido en Render, el tablero muestra la página de error de servicio; al recargar despierta.
- La ocupación es una foto de hoy (estado OCUPADA / total de habitaciones), no un promedio del periodo.
