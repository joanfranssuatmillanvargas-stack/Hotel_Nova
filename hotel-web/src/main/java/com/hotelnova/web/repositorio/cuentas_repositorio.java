package com.hotelnova.web.repositorio;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.web.modelo.Rol;
import com.hotelnova.web.modelo.Usuario;
import com.hotelnova.web.modelo.UsuarioSesion;

/**
 * Usuarios de la aplicación web (colección "usuarios" de usuariosdb).
 * Las contraseñas se guardan cifradas con PBKDF2 y una sal aleatoria, nunca en texto plano.
 */
@Repository
public class cuentas_repositorio {

	private static final SecureRandom AZAR = new SecureRandom();

	private final usuario_repositorio usuarios;

	public cuentas_repositorio(usuario_repositorio usuarios) {
		this.usuarios = usuarios;
	}

	/** La primera vez que arranca, crea un usuario por cada rol del personal. */
	@EventListener(ApplicationReadyEvent.class)
	public void crearUsuariosIniciales() {
		try {
			if (usuarios.count() == 0) {
				crear("admin", "Administrador del hotel", Rol.ADMINISTRADOR, null, "admin123");
				crear("recepcion", "Recepcionista", Rol.RECEPCIONISTA, null, "recepcion123");
				crear("servicios", "Personal de servicios", Rol.SERVICIOS, null, "servicios123");
				crear("limpieza", "Personal de limpieza", Rol.LIMPIEZA, null, "limpieza123");
			}
		} catch (RuntimeException ex) {
			// si la base de datos no responde al arrancar, se intentará de nuevo en el próximo arranque
		}
	}

	/** PLA-02: inicio de sesión. */
	public UsuarioSesion iniciarSesion(String usuario, String clave) {
		Optional<Usuario> u = usuarios.findByUsuario(usuario == null ? "" : usuario.trim().toLowerCase());
		if (u.isEmpty() || clave == null || !hash(clave, u.get().getSal()).equals(u.get().getClaveHash())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
		}
		if (!u.get().isActivo()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario está desactivado; habla con el administrador");
		}
		return sesion(u.get());
	}

	/** PLA-03: el administrador crea usuarios con su rol. HUE-06: el huésped crea su cuenta. */
	public Usuario crear(String usuario, String nombre, Rol rol, String documento, String clave) {
		if (usuario == null || !usuario.trim().matches("[a-zA-Z0-9._-]{3,30}")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"El usuario debe tener entre 3 y 30 caracteres (letras, números, punto, guion)");
		}
		if (clave == null || clave.length() < 6) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres");
		}
		if (rol == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Elige el rol del usuario");
		}
		String login = usuario.trim().toLowerCase();
		if (usuarios.existsByUsuario(login)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario " + login + " ya existe");
		}
		Usuario u = new Usuario();
		u.setUsuario(login);
		u.setNombre(nombre == null || nombre.isBlank() ? login : nombre.trim());
		u.setRol(rol);
		u.setDocumento(documento);
		String sal = sal();
		u.setSal(sal);
		u.setClaveHash(hash(clave, sal));
		u.setActivo(true);
		return usuarios.save(u);
	}

	public void validarDisponible(String usuario) {
		if (usuario != null && usuarios.existsByUsuario(usuario.trim().toLowerCase())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario " + usuario.trim().toLowerCase() + " ya existe");
		}
	}

	public List<Usuario> listar() {
		return usuarios.findAllByOrderByRolAscUsuarioAsc();
	}

	public void cambiarActivo(String id, String quien) {
		Usuario u = usuarios.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario"));
		if (u.getUsuario().equals(quien)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No puedes desactivar tu propio usuario");
		}
		u.setActivo(!u.isActivo());
		usuarios.save(u);
	}

	private static UsuarioSesion sesion(Usuario u) {
		UsuarioSesion s = new UsuarioSesion();
		s.setId(u.getId());
		s.setUsuario(u.getUsuario());
		s.setNombre(u.getNombre());
		s.setRol(u.getRol());
		s.setDocumento(u.getDocumento());
		return s;
	}

	private static String sal() {
		byte[] b = new byte[16];
		AZAR.nextBytes(b);
		return Base64.getEncoder().encodeToString(b);
	}

	private static String hash(String clave, String sal) {
		try {
			KeySpec spec = new PBEKeySpec(clave.toCharArray(), Base64.getDecoder().decode(sal), 65536, 256);
			byte[] h = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
			return Base64.getEncoder().encodeToString(h);
		} catch (Exception ex) {
			throw new IllegalStateException("No se pudo cifrar la contraseña", ex);
		}
	}
}
