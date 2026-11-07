package com.hotelnova.web.controlador;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Inicio provisional de los roles que reciben sus funciones en el R2-Sprint 2 y 3 (ADR-004, REQ-HN-002).
 * Cada pantalla se reemplaza por la real en su sprint, sin cambiar la ruta ni los permisos.
 */
@Controller
public class roles_nuevos_web {

	@GetMapping("/gerencia")
	public String gerencia(Model model) {
		return provisional(model, "Gerencia", "bi-graph-up", "R2-Sprint 3 (18/11 – 24/11)", List.of(
				"Tablero con la ocupación del día, la tarifa promedio por noche y los ingresos del periodo (PLA-10)",
				"Aprobar o rechazar las temporadas que crea el administrador (HAB-20)"));
	}

	@GetMapping("/caja")
	public String caja(Model model) {
		return provisional(model, "Caja", "bi-cash-coin", "R2-Sprint 2 (11/11 – 17/11)", List.of(
				"Ver las facturas pendientes y registrar su pago: efectivo, tarjeta o transferencia (SER-11)",
				"Cierre de caja del día con los totales por medio de pago (SER-12)"));
	}

	@GetMapping("/supervision")
	public String supervision(Model model) {
		return provisional(model, "Supervisión de limpieza", "bi-clipboard-check", "R2-Sprint 2 (11/11 – 17/11)", List.of(
				"Asignar cada habitación por limpiar a una camarera y reasignarla (HAB-18)",
				"Cada camarera verá solo las habitaciones que tiene asignadas (HAB-19)"));
	}

	@GetMapping("/agente")
	public String agente(Model model) {
		return provisional(model, "Reservas por teléfono y correo", "bi-headset", "R2-Sprint 2 (11/11 – 17/11)", List.of(
				"Crear reservas indicando el canal: teléfono, correo, WhatsApp, web o mostrador (RES-11)",
				"Registrar rápido a un huésped y modificar o cancelar sus reservas (RES-12)"));
	}

	private static String provisional(Model model, String titulo, String icono, String sprint, List<String> funciones) {
		model.addAttribute("titulo", titulo);
		model.addAttribute("icono", icono);
		model.addAttribute("sprint", sprint);
		model.addAttribute("funciones", funciones);
		return "rol-provisional";
	}
}
