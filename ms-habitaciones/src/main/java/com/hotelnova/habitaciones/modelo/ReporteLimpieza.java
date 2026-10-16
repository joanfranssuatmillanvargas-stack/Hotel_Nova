package com.hotelnova.habitaciones.modelo;



/** Datos que envía el personal de limpieza (marcar limpia o reportar daño). */
public class ReporteLimpieza {

	private String empleado;
	private String descripcion;

	public String getEmpleado() {
		return empleado;
	}

	public void setEmpleado(String empleado) {
		this.empleado = empleado;
	}
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
