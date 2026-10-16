package com.hotelnova.habitaciones.modelo;



/** Datos para cambiar el estado de una habitación. */
public class CambioEstado {

	private EstadoHabitacion estado;
	private String usuario;

	public EstadoHabitacion getEstado() {
		return estado;
	}

	public void setEstado(EstadoHabitacion estado) {
		this.estado = estado;
	}
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}
