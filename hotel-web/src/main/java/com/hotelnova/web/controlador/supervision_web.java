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

import com.hotelnova.web.modelo.Usuario;
import com.hotelnova.web.modelo.UsuarioSesion;
import com.hotelnova.web.repositorio.cuentas_repositorio;
import com.hotelnova.web.repositorio.microservicios_repositorio;

import jakarta.servlet.http.HttpSession;

/**
 * Pantalla del supervisor de limpieza (HAB-18; R2-Sprint 2, T021). Solo entra el rol SUPERVISOR_LIMPIEZA.
 * Reparte las habitaciones en LIMPIEZA entre las camareras activas (RN-09, ADR-005).
 */
@Controller
@RequestMapping("/supervision")
public class supervision_web {

	private final microservicios_repositorio ms;
	private final cuentas_repositorio cuentas;

	public supervision_web(microservicios_repositorio ms, cuentas_repositorio cuentas) {
		this.ms = ms;
		this.cuentas = cuentas;
	}

	/** HAB-18 y REQ-HN-017: todas las habitaciones por limpiar, con la camarera de cada una. */
	@GetMapping
	public String inicio(Model model) {
		model.addAttribute("pendientes", ms.lista(HABITACIONES, "/api/habitaciones/limpieza"));
		model.addAttribute("camareras", cuentas.camareras());
		return "supervision/inicio";
	}

	/** HAB-18 (REQ-HN-015, 016): asigna o reasigna; la camarera debe tener rol de limpieza y estar activa. */
	@PostMapping("/{id}/asignacion")
	public String asignar(@PathVariable("id") String id, @RequestParam("camarera") String camarera, HttpSession sesion,
			RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/supervision", () -> {
			Usuario c = cuentas.camarera(camarera);
			Map<String, Object> h = ms.post(HABITACIONES, "/api/habitaciones/" + seg(id) + "/asignacion",
					Map.of("camarera", c.getUsuario(), "supervisor", yo.getUsuario()));
			return "Habitación " + h.get("numero") + " asignada a " + c.getNombre() + " (" + c.getUsuario() + ")";
		});
	}
}
