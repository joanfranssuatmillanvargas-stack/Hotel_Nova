package com.hotelnova.habitaciones.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.habitaciones.modelo.EstadoHabitacion;
import com.hotelnova.habitaciones.modelo.Habitacion;
import com.hotelnova.habitaciones.modelo.HabitacionMantenimiento;
import com.hotelnova.habitaciones.modelo.Novedad;
import com.hotelnova.habitaciones.modelo.TipoNovedad;

/**
 * T007 y T008 (R2-Sprint 1): pruebas del técnico de mantenimiento, escritas antes del código.
 * HAB-16: lista de habitaciones en mantenimiento (AC-006.1 a 006.3).
 * HAB-17: registro de la reparación (AC-007.1, 007.2, 008.1, 008.2).
 */
class MantenimientoTest {

	private habitacion_repositorio habitaciones;
	private novedad_repositorio novedades;
	private reglas_repositorio reglas;

	@BeforeEach
	void preparar() {
		habitaciones = mock(habitacion_repositorio.class);
		novedades = mock(novedad_repositorio.class);
		reglas = new reglas_repositorio(mock(tipo_habitacion_repositorio.class), habitaciones,
				mock(temporada_repositorio.class), novedades, mock(externos_repositorio.class), "America/Bogota");
		when(habitaciones.save(any(Habitacion.class))).thenAnswer(i -> i.getArgument(0));
		when(novedades.save(any(Novedad.class))).thenAnswer(i -> i.getArgument(0));
	}

	// ---------------------------------------------------------------- HAB-16

	@Test
	void ac006_1_listaSoloLasDeMantenimientoConSuDano() {
		Habitacion h204 = habitacion("h204", "204", 2, EstadoHabitacion.MANTENIMIENTO);
		when(habitaciones.findByEstadoOrderByNumeroAsc(EstadoHabitacion.MANTENIMIENTO)).thenReturn(List.of(h204));
		when(novedades.findByHabitacionIdOrderByFechaDesc("h204")).thenReturn(List.of(
				novedad("h204", TipoNovedad.DANO, "Grifo del baño goteando", "limpieza", LocalDateTime.of(2026, 11, 5, 8, 30))));

		List<HabitacionMantenimiento> lista = reglas.enMantenimiento();

		assertEquals(1, lista.size());
		HabitacionMantenimiento m = lista.get(0);
		assertEquals("204", m.getNumero());
		assertEquals(2, m.getPiso());
		assertEquals("Grifo del baño goteando", m.getDano());
		assertEquals("limpieza", m.getReportadoPor());
		assertEquals(LocalDateTime.of(2026, 11, 5, 8, 30), m.getFechaReporte());
	}

	@Test
	void ac006_2_conVariosDanosMuestraElMasReciente() {
		Habitacion h305 = habitacion("h305", "305", 3, EstadoHabitacion.MANTENIMIENTO);
		when(habitaciones.findByEstadoOrderByNumeroAsc(EstadoHabitacion.MANTENIMIENTO)).thenReturn(List.of(h305));
		// el repositorio entrega las novedades de la más reciente a la más antigua
		when(novedades.findByHabitacionIdOrderByFechaDesc("h305")).thenReturn(List.of(
				novedad("h305", TipoNovedad.LIMPIEZA, "Habitación limpia", "limpieza", LocalDateTime.of(2026, 11, 6, 15, 0)),
				novedad("h305", TipoNovedad.DANO, "Aire no enfría", "limpieza", LocalDateTime.of(2026, 11, 6, 9, 0)),
				novedad("h305", TipoNovedad.DANO, "Bombillo fundido", "limpieza", LocalDateTime.of(2026, 11, 3, 10, 0))));

		List<HabitacionMantenimiento> lista = reglas.enMantenimiento();

		assertEquals(1, lista.size());
		assertEquals("Aire no enfría", lista.get(0).getDano());
	}

	@Test
	void ac006_3_sinHabitacionesEnMantenimientoLaListaQuedaVacia() {
		when(habitaciones.findByEstadoOrderByNumeroAsc(EstadoHabitacion.MANTENIMIENTO)).thenReturn(List.of());

		assertTrue(reglas.enMantenimiento().isEmpty());
	}

	// ---------------------------------------------------------------- HAB-17

	@Test
	void ac007_1_laReparacionPasaLaHabitacionALimpiezaYQuedaRegistrada() {
		when(habitaciones.findById("h204")).thenReturn(Optional.of(habitacion("h204", "204", 2, EstadoHabitacion.MANTENIMIENTO)));

		Habitacion resultado = reglas.registrarReparacion("h204", "mantenimiento", "Se cambió el empaque del grifo");

		assertEquals(EstadoHabitacion.LIMPIEZA, resultado.getEstado());
		assertEquals("mantenimiento", resultado.getActualizadoPor());
		ArgumentCaptor<Novedad> guardada = ArgumentCaptor.forClass(Novedad.class);
		verify(novedades).save(guardada.capture());
		assertEquals(TipoNovedad.REPARACION, guardada.getValue().getTipo());
		assertEquals("mantenimiento", guardada.getValue().getEmpleado());
		assertEquals("Se cambió el empaque del grifo", guardada.getValue().getDescripcion());
		assertEquals("204", guardada.getValue().getNumeroHabitacion());
	}

	@Test
	void ac007_2_sinDescripcionSeRechaza() {
		when(habitaciones.findById("h204")).thenReturn(Optional.of(habitacion("h204", "204", 2, EstadoHabitacion.MANTENIMIENTO)));

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.registrarReparacion("h204", "mantenimiento", "  "));

		assertEquals(400, ex.getStatusCode().value());
		assertEquals("Falta la descripción de la reparación", ex.getReason());
		verify(novedades, never()).save(any());
	}

	@Test
	void ac008_1_unaHabitacionDisponibleNoSeRepara() {
		when(habitaciones.findById("h101")).thenReturn(Optional.of(habitacion("h101", "101", 1, EstadoHabitacion.DISPONIBLE)));

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.registrarReparacion("h101", "mantenimiento", "Revisión"));

		assertEquals(409, ex.getStatusCode().value());
		assertEquals("La habitación 101 está DISPONIBLE: solo se registran reparaciones de habitaciones en MANTENIMIENTO",
				ex.getReason());
		verify(novedades, never()).save(any());
		verify(habitaciones, never()).save(any());
	}

	@Test
	void ac008_2_unaHabitacionYaReparadaNoSeReparaDosVeces() {
		when(habitaciones.findById("h204")).thenReturn(Optional.of(habitacion("h204", "204", 2, EstadoHabitacion.LIMPIEZA)));

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.registrarReparacion("h204", "mantenimiento", "Se cambió el empaque del grifo"));

		assertEquals(409, ex.getStatusCode().value());
		verify(novedades, never()).save(any());
	}

	// ---------------------------------------------------------------- ayudas

	private static Habitacion habitacion(String id, String numero, int piso, EstadoHabitacion estado) {
		Habitacion h = new Habitacion();
		h.setId(id);
		h.setNumero(numero);
		h.setPiso(piso);
		h.setEstado(estado);
		return h;
	}

	private static Novedad novedad(String habitacionId, TipoNovedad tipo, String descripcion, String empleado,
			LocalDateTime fecha) {
		Novedad n = new Novedad();
		n.setHabitacionId(habitacionId);
		n.setTipo(tipo);
		n.setDescripcion(descripcion);
		n.setEmpleado(empleado);
		n.setFecha(fecha);
		return n;
	}
}
