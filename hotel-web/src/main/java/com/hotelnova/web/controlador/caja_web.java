package com.hotelnova.web.controlador;

import static com.hotelnova.web.repositorio.microservicios_repositorio.SERVICIOS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.consulta;
import static com.hotelnova.web.repositorio.microservicios_repositorio.seg;

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
 * Pantallas del cajero (SER-11, SER-12; R2-Sprint 2, T015). Solo entra el rol CAJERO (FiltroSesion, REQ-HN-011):
 * la recepcionista genera la factura en el check-out, pero ya no registra pagos (RN-05).
 */
@Controller
@RequestMapping("/caja")
public class caja_web {

	private final microservicios_repositorio ms;

	public caja_web(microservicios_repositorio ms) {
		this.ms = ms;
	}

	/** SER-11: bandeja con las facturas PENDIENTES. */
	@GetMapping
	public String inicio(Model model) {
		model.addAttribute("facturas", ms.lista(SERVICIOS, "/api/facturas" + consulta("estado", "PENDIENTE")));
		return "caja/inicio";
	}

	/** SER-11 (REQ-HN-009, 010): registra el pago con el medio elegido y el cajero de la sesión. */
	@PostMapping("/facturas/{id}/pago")
	public String pagar(@PathVariable("id") String id, @RequestParam("medio") String medio, HttpSession sesion,
			RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/caja", () -> {
			Map<String, Object> f = ms.post(SERVICIOS,
					"/api/facturas/" + seg(id) + "/pago" + consulta("medio", medio, "cajero", yo.getUsuario()), null);
			return "Pago registrado: factura No. " + f.get("numero") + " PAGADA en " + medio.toLowerCase();
		});
	}

	/** SER-12 (REQ-HN-012): cierre de caja del día elegido (por defecto hoy) para el cajero de la sesión. */
	@GetMapping("/cierre")
	public String cierre(@RequestParam(name = "fecha", required = false) String fecha, HttpSession sesion, Model model) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		String dia = Acciones.vacio(fecha) ? Acciones.texto(model.getAttribute("hoy")) : fecha;
		model.addAttribute("fecha", dia);
		try {
			model.addAttribute("cierre", ms.objeto(SERVICIOS, "/api/facturas/cierre" + consulta("fecha", dia, "cajero", yo.getUsuario())));
		} catch (ResponseStatusException ex) {
			model.addAttribute("error", ex.getReason());
		}
		return "caja/cierre";
	}
}
