package com.hotelnova.huespedes.repositorio;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.huespedes.modelo.Acompanante;
import com.hotelnova.huespedes.modelo.Huesped;

/**
 * Reglas de negocio de ms-huespedes. El huésped se identifica por su documento (único).
 * Los errores se lanzan como ResponseStatusException con su código HTTP.
 */
@Repository
public class reglas_repositorio {

	private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
	private static final int MAX_ACOMPANANTES = 6;

	private final huesped_repositorio huespedes;
	private final externos_repositorio externos;

	public reglas_repositorio(huesped_repositorio huespedes, externos_repositorio externos) {
		this.huespedes = huespedes;
		this.externos = externos;
	}

	public Huesped buscar(String documento) {
		return huespedes.findByDocumento(documento)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un huésped con documento " + documento));
	}

	/** HUE-03: busca por documento o nombre, sin importar mayúsculas ni tildes. */
	public List<Huesped> listar(String texto) {
		List<Huesped> todos = huespedes.findAllByOrderByApellidosAscNombresAsc();
		if (texto == null || texto.isBlank()) {
			return todos;
		}
		String q = normalizar(texto);
		return todos.stream().filter(h -> normalizar(h.getDocumento()).contains(q)
				|| normalizar(h.getNombres() + " " + h.getApellidos()).contains(q)).toList();
	}

	/** HUE-01: documento único y correo válido. */
	public Huesped crear(Huesped datos) {
		validar(datos);
		if (huespedes.existsByDocumento(datos.getDocumento().trim())) {
			throw conflicto("Ya existe un huésped con documento " + datos.getDocumento().trim());
		}
		datos.setId(null);
		datos.setDocumento(datos.getDocumento().trim());
		datos.setEstadias(0);
		datos.setFrecuente(false);
		if (datos.getAcompanantes() == null) {
			datos.setAcompanantes(new ArrayList<>());
		}
		return huespedes.save(datos);
	}

	/** HUE-02 / HUE-07: edita los datos de contacto; el documento no cambia. */
	public Huesped actualizar(String documento, Huesped datos) {
		Huesped actual = buscar(documento);
		datos.setDocumento(documento);
		validar(datos);
		actual.setNombres(datos.getNombres().trim());
		actual.setApellidos(datos.getApellidos().trim());
		actual.setTelefono(datos.getTelefono());
		actual.setCorreo(datos.getCorreo().trim());
		actual.setNacionalidad(datos.getNacionalidad());
		return huespedes.save(actual);
	}

	/** HUE-04: solo se elimina si nunca ha tenido reservas. */
	public void eliminar(String documento) {
		Huesped h = buscar(documento);
		List<Map<String, Object>> reservas = externos.reservasDe(documento);
		if (!reservas.isEmpty()) {
			throw conflicto("No se puede eliminar a " + h.getNombres() + " " + h.getApellidos() + ": tiene "
					+ reservas.size() + " reserva(s) registradas");
		}
		huespedes.delete(h);
	}

	/** HUE-05: agrega un acompañante (sin repetir documento). */
	public Huesped agregarAcompanante(String documento, Acompanante a) {
		Huesped h = buscar(documento);
		if (a.getNombre() == null || a.getNombre().isBlank() || a.getDocumento() == null || a.getDocumento().isBlank()) {
			throw invalido("Escribe el nombre y el documento del acompañante");
		}
		if (a.getDocumento().trim().equals(documento)) {
			throw invalido("El acompañante no puede tener el mismo documento del huésped");
		}
		boolean repetido = h.getAcompanantes().stream().anyMatch(x -> x.getDocumento().equals(a.getDocumento().trim()));
		if (repetido) {
			throw conflicto("El acompañante con documento " + a.getDocumento().trim() + " ya está registrado");
		}
		if (h.getAcompanantes().size() >= MAX_ACOMPANANTES) {
			throw conflicto("Un huésped puede tener máximo " + MAX_ACOMPANANTES + " acompañantes");
		}
		a.setNombre(a.getNombre().trim());
		a.setDocumento(a.getDocumento().trim());
		h.getAcompanantes().add(a);
		return huespedes.save(h);
	}

	public Huesped quitarAcompanante(String documento, String documentoAcompanante) {
		Huesped h = buscar(documento);
		boolean quitado = h.getAcompanantes().removeIf(x -> x.getDocumento().equals(documentoAcompanante));
		if (!quitado) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El huésped no tiene un acompañante con documento " + documentoAcompanante);
		}
		return huespedes.save(h);
	}

	/** HUE-08: historial de estadías consultando ms-reservas. */
	public List<Map<String, Object>> historial(String documento) {
		buscar(documento);
		List<Map<String, Object>> lista = new ArrayList<>(externos.reservasDe(documento));
		lista.sort((a, b) -> String.valueOf(b.get("llegada")).compareTo(String.valueOf(a.get("llegada"))));
		return lista;
	}

	/** HUE-09: lo llama ms-reservas al cerrar una estadía; con 3 o más es huésped frecuente. */
	public Huesped actualizarEstadias(String documento, int total) {
		Huesped h = buscar(documento);
		h.setEstadias(total);
		h.setFrecuente(total >= 3);
		return huespedes.save(h);
	}

	private void validar(Huesped d) {
		obligatorio(d.getDocumento(), "el documento");
		obligatorio(d.getNombres(), "los nombres");
		obligatorio(d.getApellidos(), "los apellidos");
		obligatorio(d.getCorreo(), "el correo");
		if (!CORREO.matcher(d.getCorreo().trim()).matches()) {
			throw invalido("El correo " + d.getCorreo() + " no tiene un formato válido");
		}
	}

	private static String normalizar(String s) {
		String sinTildes = Normalizer.normalize(s == null ? "" : s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		return sinTildes.toLowerCase().trim();
	}

	private void obligatorio(String valor, String campo) {
		if (valor == null || valor.isBlank()) {
			throw invalido("Falta " + campo + " del huésped");
		}
	}

	private ResponseStatusException conflicto(String mensaje) {
		return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
	}

	private ResponseStatusException invalido(String mensaje) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
	}
}
