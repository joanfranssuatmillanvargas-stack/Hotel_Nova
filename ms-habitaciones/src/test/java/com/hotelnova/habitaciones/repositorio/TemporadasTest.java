package com.hotelnova.habitaciones.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.habitaciones.modelo.EstadoHabitacion;
import com.hotelnova.habitaciones.modelo.EstadoTemporada;
import com.hotelnova.habitaciones.modelo.Habitacion;
import com.hotelnova.habitaciones.modelo.Temporada;
import com.hotelnova.habitaciones.modelo.TipoHabitacion;

/**
 * T022 (R2-Sprint 3, PBI-07): pruebas de las temporadas con aprobación del gerente, escritas antes del código.
 * HAB-20: AC-020.1, AC-021.1 y AC-021.2 (REQ-HN-020, 021) y los bordes de ADR-008.
 * Ejemplo de la spec: tipo Doble a $200.000; temporada del 15/12 al 15/01 a $300.000; noche del 20/12.
 */
class TemporadasTest {

	private static final LocalDate INICIO = LocalDate.of(2026, 12, 15);
	private static final LocalDate FIN = LocalDate.of(2027, 1, 15);
	private static final LocalDate NOCHE = LocalDate.of(2026, 12, 20);

	private temporada_repositorio temporadas;
	private reglas_repositorio reglas;
	private final List<Temporada> guardadas = new ArrayList<>();

	@BeforeEach
	void preparar() {
		tipo_habitacion_repositorio tipos = mock(tipo_habitacion_repositorio.class);
		habitacion_repositorio habitaciones = mock(habitacion_repositorio.class);
		temporadas = mock(temporada_repositorio.class);
		reglas = new reglas_repositorio(tipos, habitaciones, temporadas, mock(novedad_repositorio.class),
				mock(externos_repositorio.class), "America/Bogota");

		TipoHabitacion doble = new TipoHabitacion();
		doble.setId("doble");
		doble.setNombre("Doble");
		doble.setTarifaBase(200_000);
		when(tipos.findById("doble")).thenReturn(Optional.of(doble));

		Habitacion h201 = new Habitacion();
		h201.setId("h201");
		h201.setNumero("201");
		h201.setTipoId("doble");
		h201.setEstado(EstadoHabitacion.DISPONIBLE);
		when(habitaciones.findById("h201")).thenReturn(Optional.of(h201));

		// el repositorio simulado guarda en una lista; el id lo pone "Mongo" al crear
		when(temporadas.save(any(Temporada.class))).thenAnswer(i -> {
			Temporada t = i.getArgument(0);
			if (t.getId() == null) {
				t.setId("t" + (guardadas.size() + 1));
			}
			guardadas.removeIf(g -> g.getId().equals(t.getId()));
			guardadas.add(t);
			return t;
		});
		when(temporadas.findByTipoId("doble")).thenAnswer(i -> new ArrayList<>(guardadas));
		when(temporadas.findById(any())).thenAnswer(i -> guardadas.stream()
				.filter(g -> g.getId().equals(i.getArgument(0))).findFirst());
	}

	@Test
	void ac020_1_laTemporadaNuevaQuedaPendienteYNoCambiaLaTarifa() {
		Temporada creada = reglas.guardarTemporada(null, altaDiciembre());

		assertEquals(EstadoTemporada.PENDIENTE, creada.getEstado());
		assertNull(creada.getRevisadaPor());
		assertEquals(200_000, valorNoche20Dic());
	}

	@Test
	void ac021_1_alAprobarlaSeAplicaSuTarifa() {
		Temporada creada = reglas.guardarTemporada(null, altaDiciembre());

		Temporada aprobada = reglas.decidirTemporada(creada.getId(), "APROBADA", "gerente");

		assertEquals(EstadoTemporada.APROBADA, aprobada.getEstado());
		assertEquals("gerente", aprobada.getRevisadaPor());
		assertTrue(aprobada.getRevisadaEn() != null);
		assertEquals(300_000, valorNoche20Dic());
	}

	@Test
	void ac021_2_alRechazarlaQuedaRechazadaYLaTarifaNoCambia() {
		Temporada creada = reglas.guardarTemporada(null, altaDiciembre());

		Temporada rechazada = reglas.decidirTemporada(creada.getId(), "RECHAZADA", "gerente");

		assertEquals(EstadoTemporada.RECHAZADA, rechazada.getEstado());
		assertEquals(200_000, valorNoche20Dic());
	}

	@Test
	void soloSeDecidenTemporadasPendientes() {
		Temporada creada = reglas.guardarTemporada(null, altaDiciembre());
		reglas.decidirTemporada(creada.getId(), "APROBADA", "gerente");

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.decidirTemporada(creada.getId(), "RECHAZADA", "gerente"));

		assertEquals(409, ex.getStatusCode().value());
		assertTrue(ex.getReason().contains("APROBADA"));
	}

	@Test
	void sinGerenteOConDecisionInvalidaResponde400() {
		Temporada creada = reglas.guardarTemporada(null, altaDiciembre());

		assertEquals(400, assertThrows(ResponseStatusException.class,
				() -> reglas.decidirTemporada(creada.getId(), "APROBADA", " ")).getStatusCode().value());
		assertEquals(400, assertThrows(ResponseStatusException.class,
				() -> reglas.decidirTemporada(creada.getId(), "PENDIENTE", "gerente")).getStatusCode().value());
		assertEquals(EstadoTemporada.PENDIENTE, creada.getEstado());
	}

	@Test
	void alEditarUnaTemporadaAprobadaVuelveAPendiente() {
		Temporada creada = reglas.guardarTemporada(null, altaDiciembre());
		reglas.decidirTemporada(creada.getId(), "APROBADA", "gerente");

		Temporada cambio = altaDiciembre();
		cambio.setTarifa(350_000);
		Temporada editada = reglas.guardarTemporada(creada.getId(), cambio);

		assertEquals(EstadoTemporada.PENDIENTE, editada.getEstado());
		assertNull(editada.getRevisadaPor());
		assertEquals(200_000, valorNoche20Dic());
	}

	@Test
	void laTemporadaDelRelease1SinEstadoSigueVigente() {
		Temporada vieja = altaDiciembre();
		vieja.setId("vieja");
		guardadas.add(vieja); // guardada antes del R2-Sprint 3: no tiene estado

		assertNull(vieja.getEstado());
		assertEquals(300_000, valorNoche20Dic());
	}

	@Test
	void unaTemporadaRechazadaNoImpideCrearOtraEnLasMismasFechas() {
		Temporada creada = reglas.guardarTemporada(null, altaDiciembre());
		reglas.decidirTemporada(creada.getId(), "RECHAZADA", "gerente");

		Temporada otra = altaDiciembre();
		otra.setTarifa(280_000);
		Temporada nueva = reglas.guardarTemporada(null, otra);

		assertEquals(EstadoTemporada.PENDIENTE, nueva.getEstado());
		assertThrows(ResponseStatusException.class, () -> reglas.guardarTemporada(null, altaDiciembre()));
	}

	private Temporada altaDiciembre() {
		Temporada t = new Temporada();
		t.setNombre("Alta de diciembre");
		t.setTipoId("doble");
		t.setInicio(INICIO);
		t.setFin(FIN);
		t.setTarifa(300_000);
		return t;
	}

	private double valorNoche20Dic() {
		return reglas.valor("h201", NOCHE, NOCHE.plusDays(1)).getValor();
	}
}
