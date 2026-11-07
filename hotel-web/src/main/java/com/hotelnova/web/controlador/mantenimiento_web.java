package com.hotelnova.web.controlador;

import static com.hotelnova.web.repositorio.microservicios_repositorio.HABITACIONES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.seg;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotelnova.web.modelo.UsuarioSesion;
import com.hotelnova.web.repositorio.microservicios_repositorio;

import jakarta.servlet.http.HttpSession;

/**
 * Pantalla del técnico de mantenimiento (HAB-16, HAB-17; R2-Sprint 1, T010), pensada para el celular.
 * Solo entra el rol MANTENIMIENTO (FiltroSesion).
 */
@Controller
@RequestMapping("/mantenimiento")
public class mantenimiento_web {

	private final microservicios_repositorio ms;

	public mantenimiento_web(microservicios_repositorio ms) {
		this.ms = ms;
	}

	/** HAB-16 (REQ-HN-006): habitaciones en MANTENIMIENTO con su último daño. */
	@GetMapping
	public String inicio(Model model) {
		model.addAttribute("habitaciones", ms.lista(HABITACIONES, "/api/habitaciones/mantenimiento"));
		return "mantenimiento/inicio";
	}

	/** HAB-17 (REQ-HN-007, 008): registra la reparación; la habitación pasa a LIMPIEZA. */
	@PostMapping("/{id}/reparacion")
	public String reparar(@PathVariable("id") String id, @RequestParam("descripcion") String descripcion,
			HttpSession sesion, RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/mantenimiento", () -> {
			Map<String, Object> h = ms.post(HABITACIONES, "/api/habitaciones/" + seg(id) + "/reparacion",
					Map.of("tecnico", yo.getUsuario(), "descripcion", descripcion));
			return "Reparación registrada: la habitación " + h.get("numero") + " pasó a limpieza";
		});
	}
}
