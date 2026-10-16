package com.hotelnova.web.controlador;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotelnova.web.modelo.Rol;
import com.hotelnova.web.modelo.UsuarioSesion;
import com.hotelnova.web.repositorio.cuentas_repositorio;
import com.hotelnova.web.repositorio.microservicios_repositorio;

import jakarta.servlet.http.HttpSession;

/** Inicio de sesión (PLA-02), cierre de sesión y registro de huéspedes (HUE-06). */
@Controller
public class acceso_web {

	private final cuentas_repositorio cuentas;
	private final microservicios_repositorio ms;

	public acceso_web(cuentas_repositorio cuentas, microservicios_repositorio ms) {
		this.cuentas = cuentas;
		this.ms = ms;
	}

	@GetMapping("/login")
	public String login(HttpSession sesion) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return yo == null ? "login" : "redirect:" + yo.getRol().getInicio();
	}

	@PostMapping("/login")
	public String entrar(@RequestParam("usuario") String usuario, @RequestParam("clave") String clave, HttpSession sesion,
			Model model) {
		try {
			UsuarioSesion yo = cuentas.iniciarSesion(usuario, clave);
			sesion.setAttribute("yo", yo);
			return "redirect:" + yo.getRol().getInicio();
		} catch (ResponseStatusException ex) {
			model.addAttribute("error", ex.getReason());
			model.addAttribute("usuario", usuario);
			return "login";
		} catch (RuntimeException ex) {
			model.addAttribute("error", "No se pudo conectar con la base de datos de usuarios (usuariosdb)");
			return "login";
		}
	}

	@GetMapping("/salir")
	public String salir(HttpSession sesion, RedirectAttributes flash) {
		sesion.invalidate();
		flash.addFlashAttribute("ok", "Sesión cerrada");
		return "redirect:/login";
	}

	@GetMapping("/registro")
	public String registro() {
		return "registro";
	}

	/** HUE-06: crea el huésped en ms-huespedes (si no existe) y su usuario con rol HUESPED. */
	@PostMapping("/registro")
	public String registrar(@RequestParam Map<String, String> form, Model model, HttpSession sesion) {
		try {
			cuentas.validarDisponible(form.get("usuario"));
			if (form.get("clave") == null || !form.get("clave").equals(form.get("clave2"))) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden");
			}
			Map<String, Object> huesped = Acciones.cuerpo(form, "documento", "nombres", "apellidos", "telefono", "correo",
					"nacionalidad");
			try {
				ms.post(microservicios_repositorio.HUESPEDES, "/api/huespedes", huesped);
			} catch (ResponseStatusException ex) {
				if (ex.getStatusCode().value() != 409) {
					throw ex; // si ya estaba registrado en recepción, se vincula su cuenta
				}
			}
			cuentas.crear(form.get("usuario"), form.get("nombres") + " " + form.get("apellidos"), Rol.HUESPED,
					form.get("documento").trim(), form.get("clave"));
			sesion.setAttribute("yo", cuentas.iniciarSesion(form.get("usuario"), form.get("clave")));
			return "redirect:/huesped";
		} catch (ResponseStatusException ex) {
			model.addAttribute("error", ex.getReason());
			model.addAttribute("form", form);
			return "registro";
		}
	}
}
