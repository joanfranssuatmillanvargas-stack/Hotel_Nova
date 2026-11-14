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
 * T012 y T016 (R2-Sprint 2): las páginas nuevas del cajero, el agente y el supervisor solo las abre su rol.
 * AC-011.1 (REQ-HN-011: solo el cajero registra pagos) y AC-014.1 (REQ-HN-014: el agente no hace check-in,
 * check-out ni pagos). La tabla de permisos es la de plan.md, sección 3 (sin cambios en el Sprint 2).
 */
class PermisosSprint2Test {

	private final FiltroSesion filtro = new FiltroSesion();

	@ParameterizedTest(name = "{0} abre {1} y vuelve a {2}")
	@CsvSource({
			"RECEPCIONISTA, /caja, /recepcion",                         // AC-011.1
			"RECEPCIONISTA, /caja/facturas/f12/pago, /recepcion",       // AC-011.1
			"ADMINISTRADOR, /caja/cierre, /admin",                      // nadie más cobra
			"SERVICIOS, /caja, /servicios",
			"AGENTE_RESERVAS, /recepcion/reservas/r1/check-in, /agente", // AC-014.1
			"AGENTE_RESERVAS, /recepcion/reservas/r1/check-out, /agente",// AC-014.1
			"AGENTE_RESERVAS, /recepcion/estadia/r1, /agente",           // AC-014.1 (cuenta y factura)
			"AGENTE_RESERVAS, /caja, /agente",                           // AC-014.1 (pagos)
			"RECEPCIONISTA, /agente/reservas/nueva, /recepcion",
			"LIMPIEZA, /supervision, /limpieza",
			"SUPERVISOR_LIMPIEZA, /limpieza, /supervision" })
	void unaPaginaDeOtroRolDevuelveAlInicioPropio(Rol rol, String ruta, String inicio) throws Exception {
		MockHttpServletResponse respuesta = new MockHttpServletResponse();

		assertFalse(filtro.preHandle(peticion(rol, ruta), respuesta, null));
		assertEquals(inicio, respuesta.getRedirectedUrl());
	}

	@ParameterizedTest(name = "{0} entra a {1}")
	@CsvSource({
			"CAJERO, /caja",
			"CAJERO, /caja/cierre",
			"AGENTE_RESERVAS, /agente/reservas/nueva",
			"AGENTE_RESERVAS, /agente/huespedes",
			"SUPERVISOR_LIMPIEZA, /supervision/h203/asignacion",
			"LIMPIEZA, /limpieza" })
	void cadaRolEntraASusPaginasNuevas(Rol rol, String ruta) throws Exception {
		assertTrue(filtro.preHandle(peticion(rol, ruta), new MockHttpServletResponse(), null));
	}

	private static MockHttpServletRequest peticion(Rol rol, String ruta) {
		MockHttpServletRequest peticion = new MockHttpServletRequest("GET", ruta);
		UsuarioSesion yo = new UsuarioSesion();
		yo.setUsuario(rol.name().toLowerCase());
		yo.setRol(rol);
		peticion.getSession(true).setAttribute("yo", yo);
		return peticion;
	}
}
