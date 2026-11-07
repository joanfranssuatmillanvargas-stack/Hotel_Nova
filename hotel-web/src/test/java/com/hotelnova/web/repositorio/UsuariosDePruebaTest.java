package com.hotelnova.web.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.hotelnova.web.modelo.Rol;
import com.hotelnova.web.modelo.Usuario;

/**
 * T002 (R2-Sprint 1, PLA-08): pruebas de los 10 roles y de los usuarios de prueba, escritas antes del código.
 * AC-001.1 y AC-003.1 a 003.3 de specs/001-hotelnova/spec.md.
 */
class UsuariosDePruebaTest {

	private usuario_repositorio usuarios;
	private cuentas_repositorio cuentas;
	private Set<String> existentes;

	@BeforeEach
	void preparar() {
		usuarios = mock(usuario_repositorio.class);
		cuentas = new cuentas_repositorio(usuarios);
		existentes = new HashSet<>();
		when(usuarios.existsByUsuario(anyString())).thenAnswer(i -> existentes.contains(i.getArgument(0)));
		when(usuarios.save(any(Usuario.class))).thenAnswer(i -> {
			existentes.add(((Usuario) i.getArgument(0)).getUsuario());
			return i.getArgument(0);
		});
	}

	@Test
	void ac001_1_elSistemaTiene10RolesConSuPropioInicio() {
		assertEquals(10, Rol.values().length);
		Set<String> inicios = new HashSet<>();
		for (Rol r : Rol.values()) {
			inicios.add(r.getInicio());
		}
		assertEquals(10, inicios.size(), "cada rol debe tener una página de inicio distinta");
		Map<Rol, String> nuevos = Map.of(Rol.GERENTE, "/gerencia", Rol.CAJERO, "/caja", Rol.MANTENIMIENTO,
				"/mantenimiento", Rol.SUPERVISOR_LIMPIEZA, "/supervision", Rol.AGENTE_RESERVAS, "/agente");
		nuevos.forEach((rol, inicio) -> assertEquals(inicio, rol.getInicio()));
	}

	@Test
	void ac003_1_enUnaBaseConUsuariosSoloSeCreanLosQueFaltan() {
		existentes.addAll(List.of("admin", "recepcion", "servicios", "limpieza"));

		cuentas.crearUsuariosIniciales();

		ArgumentCaptor<Usuario> creados = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarios, times(5)).save(creados.capture());
		Map<String, Rol> porUsuario = new HashMap<>();
		for (Usuario u : creados.getAllValues()) {
			porUsuario.put(u.getUsuario(), u.getRol());
			assertNotEquals(u.getClaveHash(), "", "la contraseña se guarda cifrada");
		}
		assertEquals(Map.of("gerente", Rol.GERENTE, "cajero", Rol.CAJERO, "mantenimiento", Rol.MANTENIMIENTO,
				"supervisor", Rol.SUPERVISOR_LIMPIEZA, "agente", Rol.AGENTE_RESERVAS), porUsuario);
	}

	@Test
	void ac003_2_unUsuarioQueYaExisteNoSeTocaAunqueEsteDesactivado() {
		existentes.addAll(List.of("admin", "recepcion", "servicios", "limpieza", "gerente", "cajero", "mantenimiento",
				"supervisor", "agente"));

		cuentas.crearUsuariosIniciales();

		verify(usuarios, never()).save(any());
	}

	@Test
	void ac003_3_enUnaBaseVaciaSeCreanLos9UsuariosDelPersonal() {
		cuentas.crearUsuariosIniciales();

		ArgumentCaptor<Usuario> creados = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarios, times(9)).save(creados.capture());
		Set<Rol> roles = EnumSet.noneOf(Rol.class);
		creados.getAllValues().forEach(u -> roles.add(u.getRol()));
		assertEquals(EnumSet.complementOf(EnumSet.of(Rol.HUESPED)), roles);
	}

	@Test
	void nfr001_laContrasenaDePruebaNoQuedaEnTextoPlano() {
		cuentas.crearUsuariosIniciales();

		ArgumentCaptor<Usuario> creados = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarios, times(9)).save(creados.capture());
		for (Usuario u : creados.getAllValues()) {
			assertNotEquals(u.getUsuario() + "123", u.getClaveHash());
			assertNotEquals(null, u.getSal());
		}
	}
}
