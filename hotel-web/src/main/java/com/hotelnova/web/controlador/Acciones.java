package com.hotelnova.web.controlador;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Ayudas comunes de los controladores web. */
final class Acciones {

	private Acciones() {
	}

	/**
	 * Ejecuta una acción contra los microservicios: si sale bien muestra el mensaje verde,
	 * si un microservicio responde con error muestra su mensaje en rojo. Luego redirige.
	 */
	static String hacer(RedirectAttributes flash, String destino, Supplier<String> accion) {
		try {
			String ok = accion.get();
			if (ok != null) {
				flash.addFlashAttribute("ok", ok);
			}
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
		}
		return "redirect:" + destino;
	}

	/** Toma del formulario solo los campos indicados que no estén vacíos. */
	static Map<String, Object> cuerpo(Map<String, String> form, String... campos) {
		Map<String, Object> m = new LinkedHashMap<>();
		for (String c : campos) {
			String v = form.get(c);
			if (v != null && !v.isBlank()) {
				m.put(c, v.trim());
			}
		}
		return m;
	}

	/** Solo permite volver a páginas de la recepción (evita redirecciones a otros sitios). */
	static String destino(String volver, String porDefecto) {
		return volver != null && volver.startsWith("/recepcion") && !volver.contains("//") ? volver : porDefecto;
	}

	static String texto(Object o) {
		return o == null ? "" : o.toString();
	}

	static boolean vacio(String s) {
		return s == null || s.isBlank();
	}
}
