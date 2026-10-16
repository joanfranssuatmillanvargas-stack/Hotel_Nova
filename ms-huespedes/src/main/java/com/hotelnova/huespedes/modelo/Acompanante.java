package com.hotelnova.huespedes.modelo;



/** Acompañante del huésped (embebido dentro del documento Huesped). */
public class Acompanante {

	private String nombre;
	private String documento;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}
}
