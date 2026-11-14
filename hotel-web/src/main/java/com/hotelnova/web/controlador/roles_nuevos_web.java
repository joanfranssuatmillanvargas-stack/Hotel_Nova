package com.hotelnova.web.controlador;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Inicio provisional de los roles que reciben sus funciones más adelante (ADR-004, REQ-HN-002).
 * Cada pantalla se reemplaza por la real en su sprint, sin cambiar la ruta ni los permisos.
 * En el R2-Sprint 2 se reemplazaron caja, supervisión y agente (caja_web, supervision_web y agente_web);
 * queda la gerencia, que llega en el R2-Sprint 3.
 */
@Controller
public class roles_nuevos_web {

	@GetMapping("/gerencia")
	public String gerencia(Model model) {
		return provisional(model, "Gerencia", "bi-graph-up", "R2-Sprint 3 (18/11 – 24/11)", List.of(
				"Tablero con la ocupación del día, la tarifa promedio por noche y los ingresos del periodo (PLA-10)",
				"Aprobar o rechazar las temporadas que crea el administrador (HAB-20)"));
	}

	private static String provisional(Model model, String titulo, String icono, String sprint, List<String> funciones) {
		model.addAttribute("titulo", titulo);
		model.addAttribute("icono", icono);
		model.addAttribute("sprint", sprint);
		model.addAttribute("funciones", funciones);
		return "rol-provisional";
	}
}
