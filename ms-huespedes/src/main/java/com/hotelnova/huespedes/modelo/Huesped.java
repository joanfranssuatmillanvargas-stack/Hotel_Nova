package com.hotelnova.huespedes.modelo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Huésped del hotel. */
@Document(collection = "huespedes")
public class Huesped {

	@Id
	private String id;

	private String documento; // único
	private String nombres;
	private String apellidos;
	private String telefono;
	private String correo;
	private String nacionalidad;
	private int estadias; // estadías cerradas
	private boolean frecuente; // 3 o más estadías
	private List<Acompanante> acompanantes = new ArrayList<>(); // embebidos

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	public int getEstadias() {
		return estadias;
	}

	public void setEstadias(int estadias) {
		this.estadias = estadias;
	}
	public boolean isFrecuente() {
		return frecuente;
	}

	public void setFrecuente(boolean frecuente) {
		this.frecuente = frecuente;
	}
	public List<Acompanante> getAcompanantes() {
		return acompanantes;
	}

	public void setAcompanantes(List<Acompanante> acompanantes) {
		this.acompanantes = acompanantes;
	}
}
