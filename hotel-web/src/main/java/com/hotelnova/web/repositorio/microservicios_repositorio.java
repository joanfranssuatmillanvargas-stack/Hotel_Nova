package com.hotelnova.web.repositorio;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.web.modelo.EstadoMicroservicio;

/**
 * La aplicación web no guarda datos del hotel: todo lo pide por REST a los
 * 4 microservicios. Este repositorio hace esas llamadas y convierte los errores
 * de los microservicios (400, 404, 409...) en un mensaje para mostrar en pantalla.
 */
@Repository
public class microservicios_repositorio {

	public static final String HABITACIONES = "ms-habitaciones";
	public static final String RESERVAS = "ms-reservas";
	public static final String HUESPEDES = "ms-huespedes";
	public static final String SERVICIOS = "ms-servicios-facturacion";

	private static final ParameterizedTypeReference<Map<String, Object>> MAPA = new ParameterizedTypeReference<>() {
	};
	private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA = new ParameterizedTypeReference<>() {
	};

	private final RestClient cliente = RestClient.create();
	private final Map<String, String> urls;

	public microservicios_repositorio(@Value("${hotelnova.ms.habitaciones}") String habitaciones,
			@Value("${hotelnova.ms.reservas}") String reservas,
			@Value("${hotelnova.ms.huespedes}") String huespedes,
			@Value("${hotelnova.ms.servicios}") String servicios) {
		this.urls = Map.of(HABITACIONES, habitaciones, RESERVAS, reservas, HUESPEDES, huespedes, SERVICIOS, servicios);
	}

	// ---------------- operaciones REST ----------------

	public List<Map<String, Object>> lista(String ms, String ruta) {
		List<Map<String, Object>> datos = llamar(ms, () -> cliente.get().uri(uri(ms, ruta)).retrieve().body(LISTA));
		return datos == null ? List.of() : datos;
	}

	public Map<String, Object> objeto(String ms, String ruta) {
		return llamar(ms, () -> cliente.get().uri(uri(ms, ruta)).retrieve().body(MAPA));
	}

	public Map<String, Object> post(String ms, String ruta, Object cuerpo) {
		return llamar(ms, () -> cuerpo == null ? cliente.post().uri(uri(ms, ruta)).retrieve().body(MAPA)
				: cliente.post().uri(uri(ms, ruta)).body(cuerpo).retrieve().body(MAPA));
	}

	public Map<String, Object> put(String ms, String ruta, Object cuerpo) {
		return llamar(ms, () -> cliente.put().uri(uri(ms, ruta)).body(cuerpo).retrieve().body(MAPA));
	}

	public Map<String, Object> patch(String ms, String ruta, Object cuerpo) {
		return llamar(ms, () -> cuerpo == null ? cliente.patch().uri(uri(ms, ruta)).retrieve().body(MAPA)
				: cliente.patch().uri(uri(ms, ruta)).body(cuerpo).retrieve().body(MAPA));
	}

	public void delete(String ms, String ruta) {
		llamar(ms, () -> cliente.delete().uri(uri(ms, ruta)).retrieve().toBodilessEntity());
	}

	/** Arma "?a=1&b=2" con los valores que no estén vacíos. */
	public static String consulta(Object... pares) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i + 1 < pares.length; i += 2) {
			Object v = pares[i + 1];
			if (v == null || v.toString().isBlank()) {
				continue;
			}
			sb.append(sb.isEmpty() ? '?' : '&').append(pares[i]).append('=')
					.append(URLEncoder.encode(v.toString().trim(), StandardCharsets.UTF_8));
		}
		return sb.toString();
	}

	/** Codifica un valor para ponerlo dentro de la ruta (por ejemplo un documento). */
	public static String seg(String valor) {
		return URLEncoder.encode(valor == null ? "" : valor.trim(), StandardCharsets.UTF_8).replace("+", "%20");
	}

	// ---------------- panel de estado ----------------

	/** Consulta el listado principal de cada microservicio para saber si responde. */
	public List<EstadoMicroservicio> estados() {
		List<EstadoMicroservicio> lista = new ArrayList<>();
		lista.add(estado(HABITACIONES, "/api/habitaciones"));
		lista.add(estado(RESERVAS, "/api/reservas"));
		lista.add(estado(HUESPEDES, "/api/huespedes"));
		lista.add(estado(SERVICIOS, "/api/servicios"));
		return lista;
	}

	private EstadoMicroservicio estado(String ms, String ruta) {
		try {
			int total = lista(ms, ruta).size();
			return new EstadoMicroservicio(ms, urls.get(ms), ruta, true, total, "Responde correctamente");
		} catch (ResponseStatusException ex) {
			return new EstadoMicroservicio(ms, urls.get(ms), ruta, false, 0, ex.getReason());
		}
	}

	// ---------------- manejo de errores ----------------

	private URI uri(String ms, String ruta) {
		return URI.create(urls.get(ms) + ruta);
	}

	private <T> T llamar(String ms, java.util.function.Supplier<T> peticion) {
		try {
			return peticion.get();
		} catch (HttpStatusCodeException ex) {
			throw new ResponseStatusException(ex.getStatusCode(), mensaje(ms, ex));
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					ms + " no responde: revisa que esté corriendo (en Render puede tardar un minuto en despertar)");
		}
	}

	private String mensaje(String ms, HttpStatusCodeException ex) {
		try {
			Map<?, ?> cuerpo = ex.getResponseBodyAs(Map.class);
			if (cuerpo != null && cuerpo.get("message") != null && !cuerpo.get("message").toString().isBlank()) {
				return cuerpo.get("message").toString();
			}
		} catch (RuntimeException ignorar) {
			// el cuerpo no era JSON
		}
		return ms + " respondió con error " + ex.getStatusCode().value();
	}
}
