package com.hotelnova.web.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.hotelnova.web.modelo.UsuarioSesion;

import jakarta.servlet.http.HttpSession;

/**
 * Página raíz. Sin sesión muestra la página de aterrizaje (presentación del sistema, pública); con sesión lleva
 * a cada usuario al inicio de su rol (REQ-HN-002).
 */
@Controller
public class inicio_web {

	@GetMapping("/")
	public String inicio(HttpSession sesion) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return yo == null ? "aterrizaje" : "redirect:" + yo.getRol().getInicio();
	}
}
