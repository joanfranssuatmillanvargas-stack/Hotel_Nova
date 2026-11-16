package com.hotelnova.reservas.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.reservas.modelo.Canal;
import com.hotelnova.reservas.modelo.EstadoReserva;
import com.hotelnova.reservas.modelo.Reserva;

/**
 * T016 (R2-Sprint 2, PBI-05): pruebas del canal y de quién toma la reserva, escritas antes del código.
 * RES-11: AC-013.1 y AC-013.2 (REQ-HN-013, RN-07, RN-08).
 * Las fechas se calculan desde hoy para que las pruebas sigan sirviendo después de noviembre.
 */
class AgenteReservasTest {

	private reserva_repositorio reservas;
	private externos_repositorio externos;
	private reglas_repositorio reglas;
	private LocalDate llegada;
	private LocalDate salida;

	@BeforeEach
	void preparar() {
		reservas = mock(reserva_repositorio.class);
		externos = mock(externos_repositorio.class);
		contador_repositorio contadores = mock(contador_repositorio.class);
		reglas = new reglas_repositorio(reservas, contadores, externos, "America/Bogota");
		llegada = LocalDate.now(ZoneId.of("America/Bogota")).plusDays(6); // "el 20/11"
		salida = llegada.plusDays(2); // "el 22/11"
		when(contadores.siguiente("reservas")).thenReturn(7L);
		when(reservas.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));
		when(externos.huesped(anyString())).thenReturn(Map.of("nombres", "Laura", "apellidos", "Gómez"));
		when(externos.fichaHabitacion("h101"))
				.thenReturn(Map.of("numero", "101", "tipoId", "t-doble", "estado", "DISPONIBLE", "capacidad", 2));
		when(externos.valorEstadia(eq("h101"), any(), any())).thenReturn(Map.of("valor", 400000));
	}

	@Test
	void ac013_1_laReservaDelAgenteGuardaElCanalYQuienLaTomo() {
		when(reservas.findByHabitacionIdAndEstadoIn(eq("h101"), any())).thenReturn(List.of());

		Reserva r = reglas.crear(datos(Canal.TELEFONO, "agente", llegada, salida));

		assertEquals(EstadoReserva.CONFIRMADA, r.getEstado());
		assertEquals(Canal.TELEFONO, r.getCanal());
		assertEquals("agente", r.getTomadaPor());
		assertEquals("R-0007", r.getCodigo());
		assertEquals("Laura Gómez", r.getNombreHuesped());
	}

	@Test
	void ac013_2_lasFechasCruzadasSeRechazanVengaDeQuienVenga() {
		Reserva existente = new Reserva();
		existente.setId("r1");
		existente.setCodigo("R-0001");
		existente.setHabitacionId("h101");
		existente.setLlegada(llegada);
		existente.setSalida(salida);
		existente.setEstado(EstadoReserva.CONFIRMADA);
		when(reservas.findByHabitacionIdAndEstadoIn(eq("h101"), any())).thenReturn(List.of(existente));

		// "del 21 al 23/11" sobre una reserva "del 20 al 22/11"
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.crear(datos(Canal.TELEFONO, "agente", llegada.plusDays(1), salida.plusDays(1))));

		assertEquals(409, ex.getStatusCode().value());
		assertTrue(ex.getReason().contains("ya está reservada"), ex.getReason());
		verify(reservas, never()).save(any());
	}

	@Test
	void rn07_sinCanalNoSeCreaLaReserva() {
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.crear(datos(null, "agente", llegada, salida)));

		assertEquals(400, ex.getStatusCode().value());
		assertTrue(ex.getReason().contains("canal"), ex.getReason());
		verify(reservas, never()).save(any());
	}

	@Test
	void rn07_sinQuienLaTomaNoSeCreaLaReserva() {
		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.crear(datos(Canal.WHATSAPP, " ", llegada, salida)));

		assertEquals(400, ex.getStatusCode().value());
		verify(reservas, never()).save(any());
	}

	@Test
	void rn07_alModificarSeConservanElCanalYQuienLaTomo() {
		Reserva r = new Reserva();
		r.setId("r9");
		r.setCodigo("R-0009");
		r.setHabitacionId("h101");
		r.setLlegada(llegada);
		r.setSalida(salida);
		r.setEstado(EstadoReserva.CONFIRMADA);
		r.setCanal(Canal.CORREO);
		r.setTomadaPor("agente");
		when(reservas.findById("r9")).thenReturn(java.util.Optional.of(r));
		when(reservas.findByHabitacionIdAndEstadoIn(eq("h101"), any())).thenReturn(List.of(r));

		Reserva cambio = new Reserva();
		cambio.setSalida(salida.plusDays(1));
		Reserva modificada = reglas.modificar("r9", cambio);

		assertEquals(Canal.CORREO, modificada.getCanal());
		assertEquals("agente", modificada.getTomadaPor());
		assertEquals(3, modificada.getNoches());
	}

	private static Reserva datos(Canal canal, String tomadaPor, LocalDate llegada, LocalDate salida) {
		Reserva d = new Reserva();
		d.setHuespedDocumento("1098765432");
		d.setHabitacionId("h101");
		d.setLlegada(llegada);
		d.setSalida(salida);
		d.setCanal(canal);
		d.setTomadaPor(tomadaPor);
		return d;
	}
}
