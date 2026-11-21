package com.hotelnova.web.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.hotelnova.web.modelo.Rol;
import com.hotelnova.web.modelo.UsuarioSesion;

/**
 * T024 (R2-Sprint 3): el tablero y las temporadas del gerente solo los abre el gerente, y el gerente no entra
 * a las páginas donde se crea, edita o cobra (AC-019.1, REQ-HN-019). Tabla de permisos: plan.md, sección 3.
 */
class PermisosSprint3Test {

	private final FiltroSesion filtro = new FiltroSesion();

	@ParameterizedTest(name = "{0} abre {1} y vuelve a {2}")
	@CsvSource({
			"GERENTE, /admin/habitaciones, /gerencia",  // AC-019.1: no crea ni edita temporadas
			"GERENTE, /admin/temporadas, /gerencia",
			"GERENTE, /recepcion/reservas, /gerencia",
			"GERENTE, /caja, /gerencia",                // AC-019.1: no cobra
			"GERENTE, /agente/reservas/nueva, /gerencia",
			"ADMINISTRADOR, /gerencia, /admin",         // HAB-20: quien crea la temporada no la aprueba
			"ADMINISTRADOR, /gerencia/temporadas/t1/aprobacion, /admin",
			"CAJERO, /gerencia, /caja",
			"RECEPCIONISTA, /gerencia/temporadas, /recepcion" })
	void unaPaginaDeOtroRolDevuelveAlInicioPropio(Rol rol, String ruta, String inicio) throws Exception {
		MockHttpServletResponse respuesta = new MockHttpServletResponse();

		assertFalse(filtro.preHandle(peticion(rol, ruta), respuesta, null));
		assertEquals(inicio, respuesta.getRedirectedUrl());
	}

	@ParameterizedTest(name = "{0} entra a {1}")
	@CsvSource({
			"GERENTE, /gerencia",
			"GERENTE, /gerencia/temporadas",
			"GERENTE, /gerencia/temporadas/t1/aprobacion" })
	void elGerenteEntraASusPaginas(Rol rol, String ruta) throws Exception {
		assertTrue(filtro.preHandle(peticion(rol, ruta), new MockHttpServletResponse(), null));
	}

	private static MockHttpServletRequest peticion(Rol rol, String ruta) {
		MockHttpServletRequest peticion = new MockHttpServletRequest("GET", ruta);
		UsuarioSesion yo = new UsuarioSesion();
		yo.setUsuario(rol.name().toLowerCase());
		yo.setRol(rol);
		peticion.getSession().setAttribute("yo", yo);
		return peticion;
	}
}
