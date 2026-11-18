package com.hotelnova.habitaciones.modelo;

/**
 * HAB-20 (REQ-HN-020, 021 · ADR-008): una temporada nueva o editada queda PENDIENTE hasta que el gerente la
 * aprueba o la rechaza. Solo las APROBADAS cambian la tarifa; las guardadas antes del R2-Sprint 3 no tienen
 * estado y siguen vigentes como aprobadas.
 */
public enum EstadoTemporada {
	PENDIENTE, APROBADA, RECHAZADA
}
