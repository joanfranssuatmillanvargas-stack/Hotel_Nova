package com.hotelnova.habitaciones.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

/**
 * Consultas por REST a otros microservicios (aquí solo a ms-reservas).
 * Nunca se lee la base de datos de otro microservicio: se le pregunta por su API.
 */
@Repository
public class externos_repositorio {

	private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA = new ParameterizedTypeReference<>() {
	};

	private final RestClient cliente = RestClient.create();
	private final String reservas;

	public externos_repositorio(@Value("${hotelnova.ms.reservas}") String reservas) {
		this.reservas = reservas;
	}

	/** Reservas confirmadas o en curso de una habitación. */
	public List<Map<String, Object>> reservasActivas(String habitacionId) {
		return lista(reservas + "/api/reservas/activas?habitacionId=" + habitacionId);
	}

	/** Reservas confirmadas que llegan en la fecha indicada. */
	public List<Map<String, Object>> llegadas(LocalDate fecha) {
		return lista(reservas + "/api/reservas/llegadas?fecha=" + fecha);
	}

	private List<Map<String, Object>> lista(String url) {
		try {
			List<Map<String, Object>> datos = cliente.get().uri(url).retrieve().body(LISTA);
			return datos == null ? List.of() : datos;
		} catch (HttpStatusCodeException ex) {
			throw new ResponseStatusException(ex.getStatusCode(), "ms-reservas respondió con error " + ex.getStatusCode().value());
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"No se pudo consultar ms-reservas: revisa que esté corriendo");
		}
	}
}
