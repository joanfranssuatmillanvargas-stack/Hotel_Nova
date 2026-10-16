package com.hotelnova.web.controlador;

import static com.hotelnova.web.repositorio.microservicios_repositorio.RESERVAS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.SERVICIOS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.consulta;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotelnova.web.modelo.UsuarioSesion;
import com.hotelnova.web.repositorio.microservicios_repositorio;

import jakarta.servlet.http.HttpSession;

/** Pantalla del personal de servicios: catálogo y registro de consumos (SER-03, SER-04). */
@Controller
@RequestMapping("/servicios")
public class servicios_web {

	private final microservicios_repositorio ms;

	public servicios_web(microservicios_repositorio ms) {
		this.ms = ms;
	}

	@GetMapping
	public String inicio(@RequestParam(name = "categoria", required = false) String categoria,
			@RequestParam(name = "reservaId", required = false) String reservaId, Model model) {
		model.addAttribute("categoria", categoria);
		model.addAttribute("catalogo", ms.lista(SERVICIOS, "/api/servicios" + consulta("categoria", categoria, "soloActivos", true)));
		model.addAttribute("activos", ms.lista(SERVICIOS, "/api/servicios" + consulta("soloActivos", true)));
		model.addAttribute("estadias", ms.lista(RESERVAS, "/api/reservas" + consulta("estado", "EN_CURSO")));
		model.addAttribute("reservaId", reservaId);
		if (!Acciones.vacio(reservaId)) {
			model.addAttribute("cuenta", ms.objeto(SERVICIOS, "/api/consumos/cuenta/" + reservaId));
		}
		return "servicios/inicio";
	}

	@PostMapping("/consumos")
	public String registrar(@RequestParam Map<String, String> form, HttpSession sesion, RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/servicios" + consulta("reservaId", form.get("reservaId")), () -> {
			Map<String, Object> datos = Acciones.cuerpo(form, "reservaId", "servicioId", "cantidad");
			datos.put("empleado", yo.getUsuario());
			Map<String, Object> c = ms.post(SERVICIOS, "/api/consumos", datos);
			return "Consumo registrado: " + c.get("cantidad") + " x " + c.get("nombreServicio");
		});
	}
}
