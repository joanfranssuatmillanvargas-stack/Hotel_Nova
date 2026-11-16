package com.hotelnova.web.controlador;

import static com.hotelnova.web.repositorio.microservicios_repositorio.HABITACIONES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.consulta;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotelnova.web.modelo.Rol;
import com.hotelnova.web.modelo.UsuarioSesion;
import com.hotelnova.web.repositorio.microservicios_repositorio;

import jakarta.servlet.http.HttpSession;

/**
 * Pantalla del personal de limpieza (HAB-11, HAB-12, HAB-13, HAB-15): pensada para el celular.
 * Desde el R2-Sprint 2 cada camarera ve solo las habitaciones que le asignó el supervisor (HAB-19, REQ-HN-017);
 * el administrador sigue viéndolas todas.
 */
@Controller
@RequestMapping("/limpieza")
public class limpieza_web {

	private final microservicios_repositorio ms;

	public limpieza_web(microservicios_repositorio ms) {
		this.ms = ms;
	}

	@GetMapping
	public String inicio(HttpSession sesion, Model model) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		boolean camarera = yo != null && yo.getRol() == Rol.LIMPIEZA;
		model.addAttribute("camarera", camarera);
		model.addAttribute("pendientes", ms.lista(HABITACIONES,
				"/api/habitaciones/limpieza" + (camarera ? consulta("camarera", yo.getUsuario()) : "")));
		model.addAttribute("habitaciones", ms.lista(HABITACIONES, "/api/habitaciones"));
		String hoy = String.valueOf(model.getAttribute("hoy"));
		List<Map<String, Object>> hoyRegistros = ms.lista(HABITACIONES, "/api/novedades" + consulta("desde", hoy, "hasta", hoy));
		model.addAttribute("registrosHoy", hoyRegistros);
		return "limpieza/inicio";
	}

	@PostMapping("/{id}/limpia")
	public String limpia(@PathVariable("id") String id, HttpSession sesion, RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/limpieza", () -> {
			Map<String, Object> h = ms.post(HABITACIONES, "/api/habitaciones/" + id + "/limpia", Map.of("empleado", yo.getUsuario()));
			return "MANTENIMIENTO".equals(h.get("estado"))
					? "Habitación " + h.get("numero") + " limpia, pero tenía un daño reportado: pasó a mantenimiento"
					: "Habitación " + h.get("numero") + " lista y disponible";
		});
	}

	@PostMapping("/danos")
	public String dano(@RequestParam("habitacionId") String habitacionId, @RequestParam("descripcion") String descripcion,
			HttpSession sesion, RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/limpieza", () -> {
			Map<String, Object> h = ms.post(HABITACIONES, "/api/habitaciones/" + habitacionId + "/danos",
					Map.of("empleado", yo.getUsuario(), "descripcion", descripcion));
			return "MANTENIMIENTO".equals(h.get("estado"))
					? "Daño reportado: la habitación " + h.get("numero") + " pasó a mantenimiento"
					: "Daño reportado en la habitación " + h.get("numero") + ": pasará a mantenimiento cuando se limpie tras la salida";
		});
	}
}
