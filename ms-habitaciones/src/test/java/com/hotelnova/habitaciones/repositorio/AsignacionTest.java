package com.hotelnova.habitaciones.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.habitaciones.modelo.EstadoHabitacion;
import com.hotelnova.habitaciones.modelo.Habitacion;
import com.hotelnova.habitaciones.modelo.Novedad;
import com.hotelnova.habitaciones.modelo.PendienteLimpieza;

/**
 * T019 (R2-Sprint 2, PBI-06): pruebas de la asignación de habitaciones a camareras, escritas antes del código.
 * HAB-18: AC-015.1, AC-015.2 y AC-016.1 (REQ-HN-015, 016). HAB-19: AC-017.1 (REQ-HN-017).
 * Que la camarera tenga rol de limpieza lo valida hotel-web (ADR-005): ver SupervisionTest.
 */
class AsignacionTest {

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

	@Test
	void ac015_1_elSupervisorAsignaUnaHabitacionEnLimpieza() {
		Habitacion h203 = habitacion("h203", "203", EstadoHabitacion.LIMPIEZA);
		when(habitaciones.findById("h203")).thenReturn(Optional.of(h203));

		Habitacion asignada = reglas.asignar("h203", "ana", "supervisor");

		assertEquals("ana", asignada.getAsignadaA());
		assertEquals("supervisor", asignada.getAsignadaPor());
		assertTrue(asignada.getAsignadaEn() != null);
		assertEquals(EstadoHabitacion.LIMPIEZA, asignada.getEstado());
	}

	@Test
	void ac015_2_alReasignarSaleDeLaListaDeLaCamareraAnterior() {
		Habitacion h203 = habitacion("h203", "203", EstadoHabitacion.LIMPIEZA);
		h203.setAsignadaA("ana");
		when(habitaciones.findById("h203")).thenReturn(Optional.of(h203));
		reglas.asignar("h203", "luz", "supervisor");
		when(habitaciones.findByEstadoOrderByNumeroAsc(EstadoHabitacion.LIMPIEZA)).thenReturn(List.of(h203));

		assertTrue(reglas.pendientesLimpieza("ana").isEmpty());
		List<PendienteLimpieza> deLuz = reglas.pendientesLimpieza("luz");
		assertEquals(1, deLuz.size());
		assertEquals("203", deLuz.get(0).getNumero());
	}

	@Test
	void ac016_1_soloSeAsignanHabitacionesEnLimpieza() {
		when(habitaciones.findById("h101")).thenReturn(Optional.of(habitacion("h101", "101", EstadoHabitacion.OCUPADA)));

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.asignar("h101", "ana", "supervisor"));

		assertEquals(409, ex.getStatusCode().value());
		assertTrue(ex.getReason().contains("101") && ex.getReason().contains("OCUPADA")
				&& ex.getReason().contains("solo se asignan habitaciones en LIMPIEZA"), ex.getReason());
		verify(habitaciones, never()).save(any());
	}

	@Test
	void req015_sinCamareraNoSeAsigna() {
		when(habitaciones.findById("h203")).thenReturn(Optional.of(habitacion("h203", "203", EstadoHabitacion.LIMPIEZA)));

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.asignar("h203", "", "supervisor"));

		assertEquals(400, ex.getStatusCode().value());
	}

	@Test
	void ac017_1_cadaCamareraVeSoloLasSuyasYElSupervisorVeTodas() {
		Habitacion h203 = habitacion("h203", "203", EstadoHabitacion.LIMPIEZA);
		h203.setAsignadaA("ana");
		Habitacion h204 = habitacion("h204", "204", EstadoHabitacion.LIMPIEZA);
		h204.setAsignadaA("ana");
		Habitacion h205 = habitacion("h205", "205", EstadoHabitacion.LIMPIEZA); // sin asignar
		when(habitaciones.findByEstadoOrderByNumeroAsc(EstadoHabitacion.LIMPIEZA)).thenReturn(List.of(h203, h204, h205));

		List<PendienteLimpieza> deAna = reglas.pendientesLimpieza("ana");
		List<PendienteLimpieza> todas = reglas.pendientesLimpieza(null);

		assertEquals(List.of("203", "204"), deAna.stream().map(PendienteLimpieza::getNumero).toList());
		assertEquals(3, todas.size());
		assertEquals("ana", todas.get(0).getAsignadaA());
		assertNull(todas.get(2).getAsignadaA());
	}

	@Test
	void req015_alQuedarLimpiaLaHabitacionSeBorraLaAsignacion() {
		Habitacion h203 = habitacion("h203", "203", EstadoHabitacion.LIMPIEZA);
		h203.setAsignadaA("ana");
		when(habitaciones.findById("h203")).thenReturn(Optional.of(h203));
		when(novedades.findByHabitacionIdOrderByFechaDesc("h203")).thenReturn(List.of());

		Habitacion limpia = reglas.marcarLimpia("h203", "ana");

		assertEquals(EstadoHabitacion.DISPONIBLE, limpia.getEstado());
		assertNull(limpia.getAsignadaA());
	}

	private static Habitacion habitacion(String id, String numero, EstadoHabitacion estado) {
		Habitacion h = new Habitacion();
		h.setId(id);
		h.setNumero(numero);
		h.setPiso(Integer.parseInt(numero.substring(0, 1)));
		h.setEstado(estado);
		return h;
	}
}
