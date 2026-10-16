package com.hotelnova.servicios.repositorio;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

/** Consulta por REST a ms-reservas (para saber si una estadía está en curso). */
@Repository
public class externos_repositorio {

	private static final ParameterizedTypeReference<Map<String, Object>> MAPA = new ParameterizedTypeReference<>() {
	};

	private final RestClient cliente = RestClient.create();
	private final String reservas;

	public externos_repositorio(@Value("${hotelnova.ms.reservas}") String reservas) {
		this.reservas = reservas;
	}

	public Map<String, Object> reserva(String reservaId) {
		try {
			return cliente.get().uri(reservas + "/api/reservas/{id}", reservaId).retrieve().body(MAPA);
		} catch (HttpStatusCodeException ex) {
			if (ex.getStatusCode().value() == 404) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe una reserva con id " + reservaId);
			}
			throw new ResponseStatusException(ex.getStatusCode(), "ms-reservas respondió con error " + ex.getStatusCode().value());
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"No se pudo consultar ms-reservas: revisa que esté corriendo");
		}
	}
}
