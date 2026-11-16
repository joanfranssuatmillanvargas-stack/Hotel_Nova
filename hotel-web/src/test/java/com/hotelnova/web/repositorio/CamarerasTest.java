package com.hotelnova.web.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.web.modelo.Rol;
import com.hotelnova.web.modelo.Usuario;

/**
 * T019 (R2-Sprint 2, PBI-06): solo se asignan habitaciones a usuarios activos con rol de limpieza.
 * REQ-HN-016 (segunda parte) y RN-09. ms-habitaciones no conoce los usuarios: lo valida hotel-web (ADR-005).
 */
class CamarerasTest {

	private usuario_repositorio usuarios;
	private cuentas_repositorio cuentas;

	@BeforeEach
	void preparar() {
		usuarios = mock(usuario_repositorio.class);
		cuentas = new cuentas_repositorio(usuarios);
		when(usuarios.findByRolOrderByUsuarioAsc(Rol.LIMPIEZA))
				.thenReturn(List.of(usuario("ana", Rol.LIMPIEZA, true), usuario("limpieza", Rol.LIMPIEZA, true),
						usuario("luz", Rol.LIMPIEZA, true), usuario("rosa", Rol.LIMPIEZA, false)));
		when(usuarios.findByUsuario("ana")).thenReturn(Optional.of(usuario("ana", Rol.LIMPIEZA, true)));
		when(usuarios.findByUsuario("rosa")).thenReturn(Optional.of(usuario("rosa", Rol.LIMPIEZA, false)));
		when(usuarios.findByUsuario("cajero")).thenReturn(Optional.of(usuario("cajero", Rol.CAJERO, true)));
		when(usuarios.findByUsuario("nadie")).thenReturn(Optional.empty());
	}

	@Test
	void rn09_elSupervisorEligeSoloEntreCamarerasActivas() {
		assertEquals(List.of("ana", "limpieza", "luz"), cuentas.camareras().stream().map(Usuario::getUsuario).toList());
	}

	@Test
	void ac015_1_unaCamareraActivaSiSePuedeElegir() {
		assertEquals("ana", cuentas.camarera("ana").getUsuario());
	}

	@Test
	void req016_unUsuarioSinRolDeLimpiezaSeRechaza() {
		ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> cuentas.camarera("cajero"));

		assertEquals(409, ex.getStatusCode().value());
		assertTrue(ex.getReason().contains("cajero") && ex.getReason().contains("limpieza"), ex.getReason());
	}

	@Test
	void req016_unaCamareraDesactivadaOInexistenteSeRechaza() {
		assertEquals(409, assertThrows(ResponseStatusException.class, () -> cuentas.camarera("rosa")).getStatusCode().value());
		assertEquals(404, assertThrows(ResponseStatusException.class, () -> cuentas.camarera("nadie")).getStatusCode().value());
	}

	private static Usuario usuario(String login, Rol rol, boolean activo) {
		Usuario u = new Usuario();
		u.setUsuario(login);
		u.setNombre(login);
		u.setRol(rol);
		u.setActivo(activo);
		return u;
	}
}
