package com.hotelnova.web.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.hotelnova.web.modelo.Rol;
import com.hotelnova.web.modelo.UsuarioSesion;

/**
 * T004 (R2-Sprint 1, PLA-09): pruebas de FiltroSesion para los 10 roles, escritas antes del código.
 * AC-004.1 a 004.4 y AC-005.1 de specs/001-hotelnova/spec.md; matriz en plan.md, sección 3.
 */
class PermisosPorRolTest {

	/** Quién entra a cada sección (plan.md, sección 3). */
	private static final Map<String, Set<Rol>> ESPERADO = Map.of(
			"/admin", Set.of(Rol.ADMINISTRADOR),
			"/recepcion", Set.of(Rol.RECEPCIONISTA, Rol.ADMINISTRADOR),
			"/huesped", Set.of(Rol.HUESPED),
			"/servicios", Set.of(Rol.SERVICIOS, Rol.ADMINISTRADOR),
			"/limpieza", Set.of(Rol.LIMPIEZA, Rol.ADMINISTRADOR),
			"/gerencia", Set.of(Rol.GERENTE),
			"/caja", Set.of(Rol.CAJERO),
			"/mantenimiento", Set.of(Rol.MANTENIMIENTO),
			"/supervision", Set.of(Rol.SUPERVISOR_LIMPIEZA),
			"/agente", Set.of(Rol.AGENTE_RESERVAS));

	private final FiltroSesion filtro = new FiltroSesion();

	@ParameterizedTest(name = "AC-004 · {0} abre {1} y vuelve a {2}")
	@CsvSource({
			"CAJERO, /recepcion/reservas, /caja",          // AC-004.1
			"MANTENIMIENTO, /admin/usuarios, /mantenimiento", // AC-004.2
			"ADMINISTRADOR, /mantenimiento, /admin",       // AC-004.3 (borde)
			"LIMPIEZA, /supervision, /limpieza",           // AC-004.4 (borde)
			"AGENTE_RESERVAS, /recepcion/reservas/nueva, /agente",
			"GERENTE, /caja, /gerencia",
			"HUESPED, /gerencia, /huesped" })
	void ac004_unaRutaDeOtroRolDevuelveAlInicioPropio(Rol rol, String ruta, String inicio) throws Exception {
		MockHttpServletResponse respuesta = new MockHttpServletResponse();

		boolean pasa = filtro.preHandle(peticion(rol, ruta), respuesta, null);

		assertFalse(pasa);
		assertEquals(inicio, respuesta.getRedirectedUrl());
	}

	@Test
	void ac005_1_sinSesionSeVaAlLogin() throws Exception {
		MockHttpServletRequest peticion = new MockHttpServletRequest("GET", "/mantenimiento");
		MockHttpServletResponse respuesta = new MockHttpServletResponse();

		assertFalse(filtro.preHandle(peticion, respuesta, null));
		assertEquals("/login", respuesta.getRedirectedUrl());
	}

	@ParameterizedTest(name = "AC-002.1 · {0} entra a su inicio")
	@EnumSource(Rol.class)
	void ac002_1_cadaRolEntraASuPropioInicio(Rol rol) throws Exception {
		MockHttpServletResponse respuesta = new MockHttpServletResponse();

		assertTrue(filtro.preHandle(peticion(rol, rol.getInicio()), respuesta, null));
		assertNull(respuesta.getRedirectedUrl());
	}

	@Test
	void nfr002_matrizCompletaDeLos10RolesContraLas10Secciones() throws Exception {
		for (Rol rol : Rol.values()) {
			for (Map.Entry<String, Set<Rol>> seccion : ESPERADO.entrySet()) {
				MockHttpServletResponse respuesta = new MockHttpServletResponse();
				boolean pasa = filtro.preHandle(peticion(rol, seccion.getKey() + "/cualquier-pagina"), respuesta, null);
				boolean deberia = seccion.getValue().contains(rol);
				assertEquals(deberia, pasa, rol + " en " + seccion.getKey());
				if (!deberia) {
					assertEquals(rol.getInicio(), respuesta.getRedirectedUrl(), rol + " en " + seccion.getKey());
				}
			}
		}
	}

	@Test
	void unaRutaQueSoloEmpiezaIgualNoSeConfundeConLaSeccion() throws Exception {
		// "/cajas" no es la sección "/caja": el filtro no debe bloquearla por prefijo
		MockHttpServletResponse respuesta = new MockHttpServletResponse();

		assertTrue(filtro.preHandle(peticion(Rol.RECEPCIONISTA, "/cajas"), respuesta, null));
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
