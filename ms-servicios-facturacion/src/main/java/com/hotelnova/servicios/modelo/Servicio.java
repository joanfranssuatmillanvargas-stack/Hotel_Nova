package com.hotelnova.servicios.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Servicio adicional del catálogo. */
@Document(collection = "servicios")
public class Servicio {

	@Id
	private String id;

	private String nombre;
	private Categoria categoria;
	private double precio;
	private boolean activo; // los inactivos no se pueden cargar

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}
	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
}
