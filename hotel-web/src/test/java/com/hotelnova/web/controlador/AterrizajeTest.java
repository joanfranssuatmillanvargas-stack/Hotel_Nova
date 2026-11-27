package com.hotelnova.web.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import com.hotelnova.web.modelo.Rol;
import com.hotelnova.web.modelo.UsuarioSesion;

/**
 * Página de aterrizaje (27/11/2026, para la presentación final): quien llega sin sesión ve la presentación del
 * sistema en "/"; quien ya inició sesión sigue yendo directo al inicio de su rol (REQ-HN-002 no cambia).
 */
class AterrizajeTest {

	private final inicio_web inicio = new inicio_web();

	@Test
	void sinSesionSeVeLaPaginaDeAterrizaje() {
		assertEquals("aterrizaje", inicio.inicio(new MockHttpSession()));
	}

	@Test
	void conSesionVaAlInicioDeSuRol() {
		MockHttpSession sesion = new MockHttpSession();
		UsuarioSesion yo = new UsuarioSesion();
		yo.setUsuario("gerente");
		yo.setRol(Rol.GERENTE);
		sesion.setAttribute("yo", yo);

		assertEquals("redirect:/gerencia", inicio.inicio(sesion));
	}
}
