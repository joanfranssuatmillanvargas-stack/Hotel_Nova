package com.hotelnova.huespedes.repositorio;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

/** Consultas por REST a ms-reservas (historial de estadías de un huésped). */
@Repository
public class externos_repositorio {

	private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA = new ParameterizedTypeReference<>() {
	};

	private final RestClient cliente = RestClient.create();
	private final String reservas;

	public externos_repositorio(@Value("${hotelnova.ms.reservas}") String reservas) {
		this.reservas = reservas;
	}

	/** Todas las reservas de un huésped, de la más reciente a la más antigua. */
	public List<Map<String, Object>> reservasDe(String documento) {
		try {
			List<Map<String, Object>> datos = cliente.get()
					.uri(reservas + "/api/reservas?huespedDocumento={doc}", documento).retrieve().body(LISTA);
			return datos == null ? List.of() : datos;
		} catch (HttpStatusCodeException ex) {
			throw new ResponseStatusException(ex.getStatusCode(), "ms-reservas respondió con error " + ex.getStatusCode().value());
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"No se pudo consultar ms-reservas: revisa que esté corriendo");
		}
	}
}
