package com.hotelnova.web.modelo;

/** Roles del sistema. Cada rol tiene su página de inicio. */
public enum Rol {

	ADMINISTRADOR("Administrador", "/admin"),
	RECEPCIONISTA("Recepcionista", "/recepcion"),
	HUESPED("Huésped", "/huesped"),
	SERVICIOS("Personal de servicios", "/servicios"),
	LIMPIEZA("Personal de limpieza", "/limpieza");

	private final String etiqueta;
	private final String inicio;

	Rol(String etiqueta, String inicio) {
		this.etiqueta = etiqueta;
		this.inicio = inicio;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public String getInicio() {
		return inicio;
	}
}
