package com.hotelnova.reservas.repositorio;

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
 * Comunicación REST con los otros 3 microservicios. ms-reservas no lee sus bases
 * de datos: les pide los datos por su API. Si el otro servicio responde un error
 * (404, 409...), se devuelve el mismo código con su mensaje.
 */
@Repository
public class externos_repositorio {

	private static final ParameterizedTypeReference<Map<String, Object>> MAPA = new ParameterizedTypeReference<>() {
	};
	private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA = new ParameterizedTypeReference<>() {
	};

	private final RestClient cliente = RestClient.create();
	private final String habitaciones;
	private final String huespedes;
	private final String servicios;

	public externos_repositorio(@Value("${hotelnova.ms.habitaciones}") String habitaciones,
			@Value("${hotelnova.ms.huespedes}") String huespedes,
			@Value("${hotelnova.ms.servicios}") String servicios) {
		this.habitaciones = habitaciones;
		this.huespedes = huespedes;
		this.servicios = servicios;
	}

	// ---------------- ms-habitaciones ----------------

	public Map<String, Object> fichaHabitacion(String habitacionId) {
		return llamar("ms-habitaciones", () -> cliente.get()
				.uri(habitaciones + "/api/habitaciones/{id}/ficha", habitacionId).retrieve().body(MAPA));
	}

	public Map<String, Object> valorEstadia(String habitacionId, LocalDate llegada, LocalDate salida) {
		return llamar("ms-habitaciones", () -> cliente.get()
				.uri(habitaciones + "/api/habitaciones/{id}/valor?llegada={l}&salida={s}", habitacionId, llegada, salida)
				.retrieve().body(MAPA));
	}

	public List<Map<String, Object>> habitacionesOfrecibles(String tipoId) {
		String url = habitaciones + "/api/habitaciones/ofrecibles" + (tipoId == null || tipoId.isBlank() ? "" : "?tipoId=" + tipoId);
		return llamar("ms-habitaciones", () -> cliente.get().uri(url).retrieve().body(LISTA));
	}

	public void cambiarEstadoHabitacion(String habitacionId, String estado, String usuario) {
		llamar("ms-habitaciones", () -> cliente.patch().uri(habitaciones + "/api/habitaciones/{id}/estado", habitacionId)
				.body(Map.of("estado", estado, "usuario", usuario)).retrieve().body(MAPA));
	}

	// ---------------- ms-huespedes ----------------

	public Map<String, Object> huesped(String documento) {
		return llamar("ms-huespedes", () -> cliente.get().uri(huespedes + "/api/huespedes/{doc}", documento)
				.retrieve().body(MAPA));
	}

	public void actualizarEstadias(String documento, long total) {
		llamar("ms-huespedes", () -> cliente.post().uri(huespedes + "/api/huespedes/{doc}/estadias", documento)
				.body(Map.of("total", total)).retrieve().body(MAPA));
	}

	// ---------------- ms-servicios-facturacion ----------------

	public Map<String, Object> generarFactura(Map<String, Object> solicitud) {
		return llamar("ms-servicios-facturacion", () -> cliente.post().uri(servicios + "/api/facturas/generar")
				.body(solicitud).retrieve().body(MAPA));
	}

	// ---------------- manejo de errores ----------------

	private <T> T llamar(String servicio, java.util.function.Supplier<T> peticion) {
		try {
			return peticion.get();
		} catch (HttpStatusCodeException ex) {
			throw new ResponseStatusException(ex.getStatusCode(), mensaje(servicio, ex));
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"No se pudo conectar con " + servicio + ": revisa que esté corriendo");
		}
	}

	private String mensaje(String servicio, HttpStatusCodeException ex) {
		try {
			Map<?, ?> cuerpo = ex.getResponseBodyAs(Map.class);
			if (cuerpo != null && cuerpo.get("message") != null) {
				return cuerpo.get("message").toString();
			}
		} catch (RuntimeException ignorar) {
			// el cuerpo no era JSON
		}
		return servicio + " respondió con error " + ex.getStatusCode().value();
	}
}
