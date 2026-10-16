package com.hotelnova.web.controlador;

import static com.hotelnova.web.repositorio.microservicios_repositorio.HABITACIONES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.HUESPEDES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.RESERVAS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.SERVICIOS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.consulta;
import static com.hotelnova.web.repositorio.microservicios_repositorio.seg;

import java.util.ArrayList;
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

import jakarta.servlet.http.HttpSession;

/** Pantallas del huésped: mis reservas, reservar en línea, mi cuenta y mis datos (RES-06, SER-05, HUE-07). */
@Controller
@RequestMapping("/huesped")
public class huesped_web {

	private final microservicios_repositorio ms;

	public huesped_web(microservicios_repositorio ms) {
		this.ms = ms;
	}

	@GetMapping
	public String inicio(HttpSession sesion, Model model) {
		UsuarioSesion yo = yo(sesion);
		List<Map<String, Object>> reservas = new ArrayList<>(
				ms.lista(RESERVAS, "/api/reservas" + consulta("huespedDocumento", yo.getDocumento())));
		reservas.sort((a, b) -> Acciones.texto(b.get("llegada")).compareTo(Acciones.texto(a.get("llegada"))));
		model.addAttribute("reservas", reservas);
		return "huesped/inicio";
	}

	@GetMapping("/reservar")
	public String reservar(@RequestParam(name = "llegada", required = false) String llegada,
			@RequestParam(name = "salida", required = false) String salida,
			@RequestParam(name = "tipoId", required = false) String tipoId, Model model) {
		model.addAttribute("tipos", ms.lista(HABITACIONES, "/api/tipos"));
		model.addAttribute("b", Map.of("llegada", Acciones.texto(llegada), "salida", Acciones.texto(salida),
				"tipoId", Acciones.texto(tipoId)));
		if (!Acciones.vacio(llegada) && !Acciones.vacio(salida)) {
			try {
				model.addAttribute("libres", ms.lista(RESERVAS, "/api/reservas/disponibilidad"
						+ consulta("llegada", llegada, "salida", salida, "tipoId", tipoId)));
			} catch (ResponseStatusException ex) {
				model.addAttribute("error", ex.getReason());
			}
		}
		return "huesped/reservar";
	}

	@PostMapping("/reservar")
	public String crearReserva(@RequestParam Map<String, String> form, HttpSession sesion, RedirectAttributes flash) {
		UsuarioSesion yo = yo(sesion);
		try {
			Map<String, Object> datos = Acciones.cuerpo(form, "habitacionId", "llegada", "salida");
			datos.put("huespedDocumento", yo.getDocumento());
			Map<String, Object> r = ms.post(RESERVAS, "/api/reservas", datos);
			flash.addFlashAttribute("ok", "¡Listo! Tu reserva " + r.get("codigo") + " en la habitación " + r.get("numeroHabitacion")
					+ " quedó confirmada");
			return "redirect:/huesped";
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
			return "redirect:/huesped/reservar" + consulta("llegada", form.get("llegada"), "salida", form.get("salida"));
		}
	}

	@PostMapping("/reservas/{id}/cancelar")
	public String cancelar(@PathVariable("id") String id, HttpSession sesion, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/huesped", () -> {
			propia(id, yo(sesion));
			return "Reserva " + ms.post(RESERVAS, "/api/reservas/" + id + "/cancelar", null).get("codigo") + " cancelada";
		});
	}

	@GetMapping("/estadia/{id}")
	public String estadia(@PathVariable("id") String id, HttpSession sesion, Model model) {
		model.addAttribute("r", propia(id, yo(sesion)));
		model.addAttribute("cuenta", ms.objeto(SERVICIOS, "/api/consumos/cuenta/" + id));
		List<Map<String, Object>> facturas = ms.lista(SERVICIOS, "/api/facturas" + consulta("reservaId", id));
		model.addAttribute("factura", facturas.isEmpty() ? null : facturas.get(0));
		return "huesped/estadia";
	}

	@GetMapping("/datos")
	public String datos(HttpSession sesion, Model model) {
		model.addAttribute("h", ms.objeto(HUESPEDES, "/api/huespedes/" + seg(yo(sesion).getDocumento())));
		return "huesped/datos";
	}

	@PostMapping("/datos")
	public String guardarDatos(@RequestParam Map<String, String> form, HttpSession sesion, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/huesped/datos", () -> {
			ms.put(HUESPEDES, "/api/huespedes/" + seg(yo(sesion).getDocumento()),
					Acciones.cuerpo(form, "nombres", "apellidos", "telefono", "correo", "nacionalidad"));
			return "Tus datos quedaron actualizados";
		});
	}

	private static UsuarioSesion yo(HttpSession sesion) {
		return (UsuarioSesion) sesion.getAttribute("yo");
	}

	/** El huésped solo puede ver y cancelar sus propias reservas. */
	private Map<String, Object> propia(String id, UsuarioSesion yo) {
		Map<String, Object> r = ms.objeto(RESERVAS, "/api/reservas/" + id);
		if (!Acciones.texto(r.get("huespedDocumento")).equals(yo.getDocumento())) {
			throw new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Esa reserva no es tuya");
		}
		return r;
	}
}
