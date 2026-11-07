package com.hotelnova.habitaciones.modelo;

/** Datos que envía el técnico de mantenimiento al registrar una reparación (HAB-17, REQ-HN-007). */
public class ReporteReparacion {

	private String tecnico; // usuario del técnico
	private String descripcion; // qué se hizo

	public String getTecnico() {
		return tecnico;
	}

	public void setTecnico(String tecnico) {
		this.tecnico = tecnico;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
