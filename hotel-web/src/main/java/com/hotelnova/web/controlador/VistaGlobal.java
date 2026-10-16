package com.hotelnova.web.controlador;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.web.modelo.UsuarioSesion;

import jakarta.servlet.http.HttpSession;

/** Datos que necesitan todas las páginas (usuario de la sesión, fecha de hoy) y página de error. */
@ControllerAdvice(annotations = Controller.class)
public class VistaGlobal {

	private final ZoneId zona;

	public VistaGlobal(@Value("${hotelnova.zona}") String zona) {
		this.zona = ZoneId.of(zona);
	}

	@ModelAttribute
	public void datosComunes(Model model, HttpSession sesion) {
		model.addAttribute("yo", (UsuarioSesion) sesion.getAttribute("yo"));
		model.addAttribute("hoy", LocalDate.now(zona).toString());
		model.addAttribute("manana", LocalDate.now(zona).plusDays(1).toString());
	}

	/** Si al abrir una página un microservicio no responde, se muestra un aviso en lugar del error 500. */
	@ExceptionHandler(ResponseStatusException.class)
	public String errorServicio(ResponseStatusException ex, Model model, HttpSession sesion) {
		datosComunes(model, sesion);
		model.addAttribute("mensaje", ex.getReason());
		return "error-servicio";
	}
}
