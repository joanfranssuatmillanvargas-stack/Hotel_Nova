package com.hotelnova.web.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Usuario de la aplicación web con su rol. */
@Document(collection = "usuarios")
public class Usuario {

	@Id
	private String id;

	private String usuario; // único
	private String nombre;
	private Rol rol;
	private String documento; // solo huéspedes: documento en ms-huespedes
	private String claveHash; // PBKDF2
	private String sal;
	private boolean activo;

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
	public String getClaveHash() {
		return claveHash;
	}

	public void setClaveHash(String claveHash) {
		this.claveHash = claveHash;
	}
	public String getSal() {
		return sal;
	}

	public void setSal(String sal) {
		this.sal = sal;
	}
	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
}
