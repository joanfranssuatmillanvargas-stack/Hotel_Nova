package com.hotelnova.web.modelo;

import java.io.Serializable;

/** Datos del usuario que inició sesión (se guardan en la sesión HTTP). */
public class UsuarioSesion implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String usuario;
	private String nombre;
	private Rol rol;
	private String documento;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}
}
