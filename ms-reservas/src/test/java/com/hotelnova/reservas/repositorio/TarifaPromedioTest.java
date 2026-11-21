package com.hotelnova.reservas.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hotelnova.reservas.modelo.EstadoReserva;
import com.hotelnova.reservas.modelo.ReporteReservas;
import com.hotelnova.reservas.modelo.Reserva;

/**
 * T024 (R2-Sprint 3, PBI-07): la tarifa promedio por noche vendida del tablero gerencial (PLA-10, REQ-HN-018).
 * Se calcula con las reservas no canceladas del periodo: valor del alojamiento / noches vendidas (ADR-009).
 */
class TarifaPromedioTest {

	private static final LocalDate DESDE = LocalDate.of(2026, 11, 1);
	private static final LocalDate HASTA = LocalDate.of(2026, 11, 30);

	private reserva_repositorio reservas;
	private reglas_repositorio reglas;

	@BeforeEach
	void preparar() {
		reservas = mock(reserva_repositorio.class);
		reglas = new reglas_repositorio(reservas, mock(contador_repositorio.class), mock(externos_repositorio.class),
				"America/Bogota");
	}

	@Test
	void ac018_1_laTarifaPromedioEsElAlojamientoVendidoEntreLasNoches() {
		when(reservas.findAllByOrderByLlegadaAsc()).thenReturn(List.of(
				reserva(LocalDate.of(2026, 11, 5), 3, 600_000, EstadoReserva.CERRADA),
				reserva(LocalDate.of(2026, 11, 19), 2, 500_000, EstadoReserva.EN_CURSO),
				reserva(LocalDate.of(2026, 11, 20), 4, 900_000, EstadoReserva.CANCELADA)));

		ReporteReservas rep = reglas.reporte(DESDE, HASTA, null);

		assertEquals(5, rep.getNoches());
		assertEquals(1_100_000, rep.getValorAlojamiento());
		assertEquals(220_000, rep.getTarifaPromedio());
	}

	@Test
	void sinNochesVendidasLaTarifaPromedioEsCero() {
		when(reservas.findAllByOrderByLlegadaAsc()).thenReturn(List.of(
				reserva(LocalDate.of(2026, 11, 20), 4, 900_000, EstadoReserva.CANCELADA)));

		ReporteReservas rep = reglas.reporte(DESDE, HASTA, null);

		assertEquals(0, rep.getNoches());
		assertEquals(0, rep.getTarifaPromedio());
	}

	private static Reserva reserva(LocalDate llegada, int noches, double valor, EstadoReserva estado) {
		Reserva r = new Reserva();
		r.setLlegada(llegada);
		r.setSalida(llegada.plusDays(noches));
		r.setNoches(noches);
		r.setValorAlojamiento(valor);
		r.setEstado(estado);
		return r;
	}
}
