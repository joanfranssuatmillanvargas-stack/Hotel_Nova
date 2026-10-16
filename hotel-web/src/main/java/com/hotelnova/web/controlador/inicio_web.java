package com.hotelnova.web.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.hotelnova.web.modelo.UsuarioSesion;

import jakarta.servlet.http.HttpSession;

/** Página raíz: lleva a cada usuario al inicio de su rol. */
@Controller
public class inicio_web {

	@GetMapping("/")
	public String inicio(HttpSession sesion) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return yo == null ? "redirect:/login" : "redirect:" + yo.getRol().getInicio();
	}
}
