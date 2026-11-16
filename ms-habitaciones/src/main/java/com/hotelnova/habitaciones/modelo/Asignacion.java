package com.hotelnova.habitaciones.modelo;

/** Lo que envía el supervisor de limpieza al asignar o reasignar una habitación (HAB-18, REQ-HN-015). */
public class Asignacion {

	private String camarera; // usuario con rol de limpieza (lo valida hotel-web, ADR-005)
	private String supervisor; // quién asigna

	public String getCamarera() {
		return camarera;
	}

	public void setCamarera(String camarera) {
		this.camarera = camarera;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
}
