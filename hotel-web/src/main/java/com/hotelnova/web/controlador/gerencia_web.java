package com.hotelnova.web.controlador;

import static com.hotelnova.web.repositorio.microservicios_repositorio.HABITACIONES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.consulta;
import static com.hotelnova.web.repositorio.microservicios_repositorio.seg;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotelnova.web.modelo.UsuarioSesion;
import com.hotelnova.web.repositorio.microservicios_repositorio;
import com.hotelnova.web.repositorio.tablero_repositorio;

import jakarta.servlet.http.HttpSession;

/**
 * Pantallas del gerente (R2-Sprint 3, T023 y T024). Solo entra el rol GERENTE.
 * PLA-10: tablero de solo consulta, sin crear, editar ni cobrar (REQ-HN-018, 019).
 * HAB-20: aprobar o rechazar las temporadas PENDIENTES que crea el administrador (REQ-HN-020, 021 · ADR-008).
 */
@Controller
@RequestMapping("/gerencia")
public class gerencia_web {

	private final microservicios_repositorio ms;
	private final tablero_repositorio tablero;

	public gerencia_web(microservicios_repositorio ms, tablero_repositorio tablero) {
		this.ms = ms;
		this.tablero = tablero;
	}

	/** PLA-10: ocupación del día, tarifa promedio y los ingresos del periodo (por defecto, el mes actual). */
	@GetMapping
	public String inicio(@RequestParam(name = "desde", required = false) String desde,
			@RequestParam(name = "hasta", required = false) String hasta, Model model) {
		LocalDate hoy = LocalDate.parse(Acciones.texto(model.getAttribute("hoy")));
		LocalDate d = fecha(desde, hoy.withDayOfMonth(1));
		LocalDate h = fecha(hasta, hoy.withDayOfMonth(hoy.lengthOfMonth()));
		try {
			model.addAttribute("t", tablero.tablero(d, h));
		} catch (ResponseStatusException ex) {
			model.addAttribute("error", ex.getReason());
			model.addAttribute("t", tablero.tablero(hoy.withDayOfMonth(1), hoy.withDayOfMonth(hoy.lengthOfMonth())));
		}
		return "gerencia/inicio";
	}

	/** HAB-20: primero las PENDIENTES; las aprobadas y rechazadas quedan como historial. */
	@GetMapping("/temporadas")
	public String temporadas(Model model) {
		Map<String, Object> nombresTipo = new LinkedHashMap<>();
		ms.lista(HABITACIONES, "/api/tipos").forEach(t -> nombresTipo.put(Acciones.texto(t.get("id")), t.get("nombre")));
		List<Map<String, Object>> todas = new ArrayList<>(ms.lista(HABITACIONES, "/api/temporadas"));
		todas.sort(Comparator.comparing((Map<String, Object> t) -> !"PENDIENTE".equals(t.get("estado"))));
		model.addAttribute("temporadas", todas);
		model.addAttribute("nombresTipo", nombresTipo);
		return "gerencia/temporadas";
	}

	/** HAB-20 (REQ-HN-021): decision = APROBADA o RECHAZADA; ms-habitaciones responde 409 si ya se decidió. */
	@PostMapping("/temporadas/{id}/aprobacion")
	public String decidir(@PathVariable("id") String id, @RequestParam("decision") String decision, HttpSession sesion,
			RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/gerencia/temporadas", () -> {
			Map<String, Object> t = ms.post(HABITACIONES, "/api/temporadas/" + seg(id) + "/aprobacion"
					+ consulta("decision", decision, "gerente", yo.getUsuario()), null);
			return "Temporada \"" + t.get("nombre") + "\" " + ("APROBADA".equals(t.get("estado"))
					? "aprobada: su tarifa ya se aplica" : "rechazada: sigue la tarifa anterior");
		});
	}

	private static LocalDate fecha(String texto, LocalDate porDefecto) {
		try {
			return Acciones.vacio(texto) ? porDefecto : LocalDate.parse(texto);
		} catch (DateTimeParseException ex) {
			return porDefecto;
		}
	}
}
