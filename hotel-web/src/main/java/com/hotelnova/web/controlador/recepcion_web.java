package com.hotelnova.web.controlador;

import static com.hotelnova.web.repositorio.microservicios_repositorio.HABITACIONES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.HUESPEDES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.RESERVAS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.SERVICIOS;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotelnova.web.modelo.UsuarioSesion;
import com.hotelnova.web.repositorio.microservicios_repositorio;

import jakarta.servlet.http.HttpSession;

/** Pantallas del recepcionista: llegadas y salidas, huéspedes, reservas, estadías y estado de habitaciones. */
@Controller
@RequestMapping("/recepcion")
public class recepcion_web {

	private final microservicios_repositorio ms;

	public recepcion_web(microservicios_repositorio ms) {
		this.ms = ms;
	}

	// ======================= Inicio: llegadas de hoy y estadías en curso =======================

	@GetMapping
	public String inicio(Model model) {
		String hoy = Acciones.texto(model.getAttribute("hoy"));
		model.addAttribute("llegadas", ms.lista(RESERVAS, "/api/reservas/llegadas" + consulta("fecha", hoy)));
		model.addAttribute("enCurso", ms.lista(RESERVAS, "/api/reservas" + consulta("estado", "EN_CURSO")));
		model.addAttribute("ocupacion", ms.objeto(HABITACIONES, "/api/habitaciones/ocupacion"));
		return "recepcion/inicio";
	}

	// ======================= Huéspedes (HUE-01 a HUE-05, HUE-08) =======================

	@GetMapping("/huespedes")
	public String huespedes(@RequestParam(name = "q", required = false) String q, Model model) {
		model.addAttribute("huespedes", ms.lista(HUESPEDES, "/api/huespedes" + consulta("q", q)));
		model.addAttribute("q", q);
		return "recepcion/huespedes";
	}

	@PostMapping("/huespedes")
	public String crearHuesped(@RequestParam Map<String, String> form, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/recepcion/huespedes", () -> {
			ms.post(HUESPEDES, "/api/huespedes",
					Acciones.cuerpo(form, "documento", "nombres", "apellidos", "telefono", "correo", "nacionalidad"));
			return "Huésped " + form.get("nombres") + " " + form.get("apellidos") + " registrado";
		});
	}

	@GetMapping("/huespedes/{documento}")
	public String huesped(@PathVariable("documento") String documento, Model model) {
		model.addAttribute("h", ms.objeto(HUESPEDES, "/api/huespedes/" + seg(documento)));
		model.addAttribute("historial", ms.lista(HUESPEDES, "/api/huespedes/" + seg(documento) + "/historial"));
		return "recepcion/huesped";
	}

	@PostMapping("/huespedes/{documento}")
	public String editarHuesped(@PathVariable("documento") String documento, @RequestParam Map<String, String> form,
			RedirectAttributes flash) {
		return Acciones.hacer(flash, "/recepcion/huespedes/" + seg(documento), () -> {
			ms.put(HUESPEDES, "/api/huespedes/" + seg(documento),
					Acciones.cuerpo(form, "nombres", "apellidos", "telefono", "correo", "nacionalidad"));
			return "Datos del huésped actualizados";
		});
	}

	@PostMapping("/huespedes/{documento}/eliminar")
	public String eliminarHuesped(@PathVariable("documento") String documento, RedirectAttributes flash) {
		try {
			ms.delete(HUESPEDES, "/api/huespedes/" + seg(documento));
			flash.addFlashAttribute("ok", "Huésped eliminado");
			return "redirect:/recepcion/huespedes";
		} catch (org.springframework.web.server.ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
			return "redirect:/recepcion/huespedes/" + seg(documento);
		}
	}

	@PostMapping("/huespedes/{documento}/acompanantes")
	public String agregarAcompanante(@PathVariable("documento") String documento, @RequestParam Map<String, String> form,
			RedirectAttributes flash) {
		return Acciones.hacer(flash, "/recepcion/huespedes/" + seg(documento), () -> {
			ms.post(HUESPEDES, "/api/huespedes/" + seg(documento) + "/acompanantes", Acciones.cuerpo(form, "nombre", "documento"));
			return "Acompañante " + form.get("nombre") + " agregado";
		});
	}

	@PostMapping("/huespedes/{documento}/acompanantes/{otro}/quitar")
	public String quitarAcompanante(@PathVariable("documento") String documento, @PathVariable("otro") String otro,
			RedirectAttributes flash) {
		return Acciones.hacer(flash, "/recepcion/huespedes/" + seg(documento), () -> {
			ms.delete(HUESPEDES, "/api/huespedes/" + seg(documento) + "/acompanantes/" + seg(otro));
			return "Acompañante quitado";
		});
	}

	// ======================= Reservas (RES-01 a RES-08) =======================

	@GetMapping("/reservas")
	public String reservas(@RequestParam(name = "estado", required = false) String estado,
			@RequestParam(name = "documento", required = false) String documento,
			@RequestParam(name = "desde", required = false) String desde,
			@RequestParam(name = "hasta", required = false) String hasta, Model model) {
		model.addAttribute("reservas", ms.lista(RESERVAS, "/api/reservas"
				+ consulta("estado", estado, "huespedDocumento", documento, "desde", desde, "hasta", hasta)));
		model.addAttribute("f", Map.of("estado", Acciones.texto(estado), "documento", Acciones.texto(documento),
				"desde", Acciones.texto(desde), "hasta", Acciones.texto(hasta)));
		return "recepcion/reservas";
	}

	/** Paso 1: buscar habitaciones libres (RES-06). Paso 2: reservar una (RES-01). */
	@GetMapping("/reservas/nueva")
	public String nuevaReserva(@RequestParam(name = "llegada", required = false) String llegada,
			@RequestParam(name = "salida", required = false) String salida,
			@RequestParam(name = "tipoId", required = false) String tipoId,
			@RequestParam(name = "documento", required = false) String documento, Model model) {
		model.addAttribute("tipos", ms.lista(HABITACIONES, "/api/tipos"));
		model.addAttribute("b", Map.of("llegada", Acciones.texto(llegada), "salida", Acciones.texto(salida),
				"tipoId", Acciones.texto(tipoId), "documento", Acciones.texto(documento)));
		if (!Acciones.vacio(llegada) && !Acciones.vacio(salida)) {
			try {
				model.addAttribute("libres", ms.lista(RESERVAS, "/api/reservas/disponibilidad"
						+ consulta("llegada", llegada, "salida", salida, "tipoId", tipoId)));
			} catch (org.springframework.web.server.ResponseStatusException ex) {
				model.addAttribute("error", ex.getReason());
			}
		}
		return "recepcion/reserva-nueva";
	}

	@PostMapping("/reservas")
	public String crearReserva(@RequestParam Map<String, String> form, RedirectAttributes flash) {
		try {
			Map<String, Object> r = ms.post(RESERVAS, "/api/reservas",
					Acciones.cuerpo(form, "huespedDocumento", "habitacionId", "llegada", "salida"));
			flash.addFlashAttribute("ok", "Reserva " + r.get("codigo") + " creada para " + r.get("nombreHuesped")
					+ " en la habitación " + r.get("numeroHabitacion"));
			return "redirect:/recepcion/reservas";
		} catch (org.springframework.web.server.ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
			return "redirect:/recepcion/reservas/nueva" + consulta("llegada", form.get("llegada"), "salida", form.get("salida"),
					"documento", form.get("huespedDocumento"));
		}
	}

	@GetMapping("/reservas/{id}/editar")
	public String editarReserva(@PathVariable("id") String id, Model model) {
		model.addAttribute("r", ms.objeto(RESERVAS, "/api/reservas/" + id));
		model.addAttribute("habitaciones", ms.lista(HABITACIONES, "/api/habitaciones"));
		return "recepcion/reserva-editar";
	}

	@PostMapping("/reservas/{id}/editar")
	public String guardarReserva(@PathVariable("id") String id, @RequestParam Map<String, String> form, RedirectAttributes flash) {
		try {
			Map<String, Object> r = ms.put(RESERVAS, "/api/reservas/" + id, Acciones.cuerpo(form, "habitacionId", "llegada", "salida"));
			flash.addFlashAttribute("ok", "Reserva " + r.get("codigo") + " modificada");
			return "redirect:/recepcion/reservas";
		} catch (org.springframework.web.server.ResponseStatusException ex) {
			flash.addFlashAttribute("error", ex.getReason());
			return "redirect:/recepcion/reservas/" + id + "/editar";
		}
	}

	@PostMapping("/reservas/{id}/cancelar")
	public String cancelar(@PathVariable("id") String id, @RequestParam(name = "volver", defaultValue = "/recepcion/reservas") String volver,
			RedirectAttributes flash) {
		return Acciones.hacer(flash, Acciones.destino(volver, "/recepcion/reservas"), () -> "Reserva " + ms.post(RESERVAS, "/api/reservas/" + id + "/cancelar", null).get("codigo") + " cancelada");
	}

	@PostMapping("/reservas/{id}/check-in")
	public String checkIn(@PathVariable("id") String id, @RequestParam(name = "volver", defaultValue = "/recepcion") String volver,
			HttpSession sesion, RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, Acciones.destino(volver, "/recepcion"), () -> {
			Map<String, Object> r = ms.post(RESERVAS, "/api/reservas/" + id + "/check-in" + consulta("usuario", yo.getUsuario()), null);
			return "Check-in de " + r.get("nombreHuesped") + " en la habitación " + r.get("numeroHabitacion");
		});
	}

	@PostMapping("/reservas/{id}/check-out")
	public String checkOut(@PathVariable("id") String id, HttpSession sesion, RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/recepcion/estadia/" + id, () -> {
			Map<String, Object> r = ms.post(RESERVAS, "/api/reservas/" + id + "/check-out" + consulta("usuario", yo.getUsuario()), null);
			return "Check-out hecho: se generó la factura y la habitación " + r.get("numeroHabitacion") + " quedó pendiente de limpieza";
		});
	}

	// ======================= Estadía: cuenta, factura y pago (SER-05 a SER-09) =======================

	@GetMapping("/estadia/{id}")
	public String estadia(@PathVariable("id") String id, Model model) {
		model.addAttribute("r", ms.objeto(RESERVAS, "/api/reservas/" + id));
		model.addAttribute("cuenta", ms.objeto(SERVICIOS, "/api/consumos/cuenta/" + id));
		List<Map<String, Object>> facturas = ms.lista(SERVICIOS, "/api/facturas" + consulta("reservaId", id));
		model.addAttribute("factura", facturas.isEmpty() ? null : facturas.get(0));
		return "recepcion/estadia";
	}

	@PostMapping("/estadia/{id}/consumos/{consumoId}/anular")
	public String anularConsumo(@PathVariable("id") String id, @PathVariable("consumoId") String consumoId,
			@RequestParam("motivo") String motivo, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/recepcion/estadia/" + id, () -> {
			ms.post(SERVICIOS, "/api/consumos/" + consumoId + "/anular", Map.of("motivo", motivo));
			return "Consumo anulado";
		});
	}

	@PostMapping("/estadia/{id}/pago")
	public String pagar(@PathVariable("id") String id, @RequestParam("facturaId") String facturaId,
			@RequestParam("medio") String medio, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/recepcion/estadia/" + id, () -> {
			Map<String, Object> f = ms.post(SERVICIOS, "/api/facturas/" + facturaId + "/pago" + consulta("medio", medio), null);
			return "Pago registrado: factura No. " + f.get("numero") + " PAGADA";
		});
	}

	// ======================= Estado de habitaciones (HAB-07) =======================

	@GetMapping("/habitaciones")
	public String habitaciones(@RequestParam(name = "estado", required = false) String estado, Model model) {
		model.addAttribute("habitaciones", ms.lista(HABITACIONES, "/api/habitaciones" + consulta("estado", estado)));
		model.addAttribute("filtroEstado", estado);
		return "recepcion/habitaciones";
	}

	@PostMapping("/habitaciones/{id}/estado")
	public String estado(@PathVariable("id") String id, @RequestParam("estado") String estado, HttpSession sesion,
			RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/recepcion/habitaciones", () -> {
			Map<String, Object> h = ms.patch(HABITACIONES, "/api/habitaciones/" + id + "/estado",
					Map.of("estado", estado, "usuario", yo.getUsuario()));
			return "La habitación " + h.get("numero") + " quedó " + h.get("estado");
		});
	}
}
