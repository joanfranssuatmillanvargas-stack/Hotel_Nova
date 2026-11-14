package com.hotelnova.web.controlador;

import static com.hotelnova.web.repositorio.microservicios_repositorio.HABITACIONES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.HUESPEDES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.RESERVAS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.consulta;
import static com.hotelnova.web.repositorio.microservicios_repositorio.seg;

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

/**
 * Pantallas del agente de reservas (RES-11, RES-12; R2-Sprint 2, T018). Solo entra el rol AGENTE_RESERVAS.
 * Reserva por teléfono, correo o WhatsApp guardando el canal y quién la tomó (REQ-HN-013), registra huéspedes y
 * modifica o cancela reservas. No tiene check-in, check-out, cuentas ni pagos (REQ-HN-014).
 */
@Controller
@RequestMapping("/agente")
public class agente_web {

	/** Canales por los que llama un cliente al agente (los de la web y el mostrador los ponen el huésped y recepción). */
	static final List<String> CANALES = List.of("TELEFONO", "CORREO", "WHATSAPP");

	private final microservicios_repositorio ms;

	public agente_web(microservicios_repositorio ms) {
		this.ms = ms;
	}

	/** RES-12: reservas por huésped y estado (por defecto las confirmadas, que son las que se pueden cambiar). */
	@GetMapping
	public String inicio(@RequestParam(name = "documento", required = false) String documento,
			@RequestParam(name = "estado", required = false) String estado, Model model) {
		String filtro = estado == null ? "CONFIRMADA" : estado;
		model.addAttribute("reservas", ms.lista(RESERVAS, "/api/reservas" + consulta("estado", filtro, "huespedDocumento", documento)));
		model.addAttribute("f", Map.of("documento", Acciones.texto(documento), "estado", filtro));
		return "agente/inicio";
	}

	/** RES-11, paso 1: disponibilidad por fechas y tipo (RES-06), y el huésped que llama. */
	@GetMapping("/reservas/nueva")
	public String nueva(@RequestParam(name = "llegada", required = false) String llegada,
			@RequestParam(name = "salida", required = false) String salida,
			@RequestParam(name = "tipoId", required = false) String tipoId,
			@RequestParam(name = "documento", required = false) String documento,
			@RequestParam(name = "canal", required = false) String canal, Model model) {
		model.addAttribute("tipos", ms.lista(HABITACIONES, "/api/tipos"));
		model.addAttribute("canales", CANALES);
		model.addAttribute("b", Map.of("llegada", Acciones.texto(llegada), "salida", Acciones.texto(salida),
				"tipoId", Acciones.texto(tipoId), "documento", Acciones.texto(documento),
				"canal", Acciones.vacio(canal) ? "TELEFONO" : canal));
		if (!Acciones.vacio(documento)) {
			try {
				model.addAttribute("huesped", ms.objeto(HUESPEDES, "/api/huespedes/" + seg(documento.trim())));
			} catch (ResponseStatusException ex) {
				model.addAttribute("huespedNuevo", true); // RES-12: no existe, se registra rápido en la misma pantalla
			}
		}
		if (!Acciones.vacio(llegada) && !Acciones.vacio(salida)) {
			try {
				model.addAttribute("libres", ms.lista(RESERVAS, "/api/reservas/disponibilidad"
						+ consulta("llegada", llegada, "salida", salida, "tipoId", tipoId)));
			} catch (ResponseStatusException ex) {
				model.addAttribute("error", ex.getReason());
			}
		}
		return "agente/reserva-nueva";
	}

	/** RES-12: registro rápido del huésped que llama; queda igual que si lo creara recepción. */
	@PostMapping("/huespedes")
	public String registrarHuesped(@RequestParam Map<String, String> form, RedirectAttributes flash) {
		String volver = "/agente/reservas/nueva" + consulta("documento", form.get("documento"), "llegada", form.get("llegada"),
				"salida", form.get("salida"), "tipoId", form.get("tipoId"), "canal", form.get("canal"));
		return Acciones.hacer(flash, volver, () -> {
			ms.post(HUESPEDES, "/api/huespedes", Acciones.cuerpo(form, "documento", "nombres", "apellidos", "telefono", "correo"));
			return "Huésped " + form.get("nombres") + " " + form.get("apellidos") + " registrado: ya puedes reservar";
		});
	}

	/** RES-11, paso 2 (REQ-HN-013): crea la reserva con el canal elegido y el agente de la sesión. */
	@PostMapping("/reservas")
	public String crear(@RequestParam Map<String, String> form, HttpSession sesion, RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		try {
			Map<String, Object> datos = Acciones.cuerpo(form, "huespedDocumento", "habitacionId", "llegada", "salida", "canal");
			datos.put("tomadaPor", yo.getUsuario());
			Map<String, Object> r = ms.post(RESERVAS, "/api/reservas", datos);
			flash.addFlashAttribute("ok", "Reserva " + r.get("codigo") + " confirmada para " + r.get("nombreHuesped")
					+ " en la habitación " + r.get("numeroHabitacion") + " (canal " + r.get("canal") + ")");
			return "redirect:/agente/reservas/" + r.get("id");
		} catch (ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
			return "redirect:/agente/reservas/nueva" + consulta("llegada", form.get("llegada"), "salida", form.get("salida"),
					"documento", form.get("huespedDocumento"), "canal", form.get("canal"));
		}
	}

	/** RES-12 y AC-014.1: detalle de la reserva para modificarla o cancelarla, sin check-in, check-out ni pagos. */
	@GetMapping("/reservas/{id}")
	public String detalle(@PathVariable("id") String id, Model model) {
		model.addAttribute("r", ms.objeto(RESERVAS, "/api/reservas/" + seg(id)));
		model.addAttribute("habitaciones", ms.lista(HABITACIONES, "/api/habitaciones"));
		return "agente/reserva";
	}

	@PostMapping("/reservas/{id}/editar")
	public String modificar(@PathVariable("id") String id, @RequestParam Map<String, String> form, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/agente/reservas/" + seg(id), () -> {
			Map<String, Object> r = ms.put(RESERVAS, "/api/reservas/" + seg(id), Acciones.cuerpo(form, "habitacionId", "llegada", "salida"));
			return "Reserva " + r.get("codigo") + " modificada";
		});
	}

	@PostMapping("/reservas/{id}/cancelar")
	public String cancelar(@PathVariable("id") String id, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/agente",
				() -> "Reserva " + ms.post(RESERVAS, "/api/reservas/" + seg(id) + "/cancelar", null).get("codigo") + " cancelada");
	}
}
