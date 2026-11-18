package com.hotelnova.web.repositorio;

import static com.hotelnova.web.repositorio.microservicios_repositorio.HABITACIONES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.HUESPEDES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.RESERVAS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.SERVICIOS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

/**
 * T024 (R2-Sprint 3, PBI-07): el tablero gerencial consulta los 4 microservicios por REST y solo lee
 * (PLA-10: AC-018.1, REQ-HN-018; ADR-009). Ejemplo de la spec: 10 habitaciones y 6 ocupadas el 19/11.
 */
class TableroGerenciaTest {

	private static final LocalDate DESDE = LocalDate.of(2026, 11, 1);
	private static final LocalDate HASTA = LocalDate.of(2026, 11, 30);
	private static final String PERIODO = "?desde=2026-11-01&hasta=2026-11-30";

	private microservicios_repositorio ms;
	private tablero_repositorio tablero;

	@BeforeEach
	void preparar() {
		ms = mock(microservicios_repositorio.class);
		tablero = new tablero_repositorio(ms);
		when(ms.objeto(HABITACIONES, "/api/habitaciones/ocupacion")).thenReturn(Map.of("total", 10,
				"porcentajeOcupacion", 60.0, "porEstado", Map.of("OCUPADA", 6, "DISPONIBLE", 3, "LIMPIEZA", 1)));
		when(ms.objeto(RESERVAS, "/api/reservas/reporte" + PERIODO))
				.thenReturn(Map.of("total", 3, "noches", 5, "valorAlojamiento", 1_100_000.0, "tarifaPromedio", 220_000.0));
		Map<String, Object> porCategoria = new LinkedHashMap<>();
		porCategoria.put("RESTAURANTE", 80_000.0);
		porCategoria.put("LAVANDERIA", 20_000.0);
		when(ms.objeto(SERVICIOS, "/api/facturas/reporte" + PERIODO)).thenReturn(Map.of("facturas", 2,
				"alojamiento", 1_000_000.0, "porCategoria", porCategoria, "iva", 209_000.0, "total", 1_309_000.0,
				"pagado", 1_309_000.0));
		when(ms.lista(HUESPEDES, "/api/huespedes")).thenReturn(List.of(Map.of(), Map.of(), Map.of(), Map.of()));
		when(ms.lista(HABITACIONES, "/api/temporadas?estado=PENDIENTE")).thenReturn(List.of(Map.of("id", "t1")));
	}

	@Test
	@SuppressWarnings("unchecked")
	void ac018_1_muestraOcupacionTarifaPromedioEIngresosPorCategoria() {
		Map<String, Object> t = tablero.tablero(DESDE, HASTA);

		assertEquals(60.0, t.get("ocupacion"));
		assertEquals(6L, t.get("ocupadas"));
		assertEquals(10L, t.get("habitaciones"));
		assertEquals(220_000.0, t.get("tarifaPromedio"));
		assertEquals(5L, t.get("noches"));
		Map<String, Double> categorias = (Map<String, Double>) t.get("ingresosPorCategoria");
		assertEquals(List.of("Alojamiento", "RESTAURANTE", "LAVANDERIA"), List.copyOf(categorias.keySet()));
		assertEquals(1_000_000.0, categorias.get("Alojamiento"));
		assertEquals(1_309_000.0, t.get("ingresosTotal"));
		assertEquals(4, t.get("huespedes"));
		assertEquals(1, t.get("temporadasPendientes"));
	}

	@Test
	void consultaLosCuatroMicroserviciosConElPeriodoElegido() {
		tablero.tablero(DESDE, HASTA);

		verify(ms).objeto(HABITACIONES, "/api/habitaciones/ocupacion");
		verify(ms).objeto(RESERVAS, "/api/reservas/reporte" + PERIODO);
		verify(ms).objeto(SERVICIOS, "/api/facturas/reporte" + PERIODO);
		verify(ms).lista(HUESPEDES, "/api/huespedes");
	}

	@Test
	void unPeriodoAlRevesResponde400SinConsultar() {
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> tablero.tablero(HASTA, DESDE));

		assertEquals(400, ex.getStatusCode().value());
		verify(ms, never()).objeto(anyString(), anyString());
	}
}
