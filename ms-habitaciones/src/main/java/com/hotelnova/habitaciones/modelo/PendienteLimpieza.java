package com.hotelnova.habitaciones.modelo;



/** Habitación pendiente de limpieza. */
public class PendienteLimpieza {

	private String habitacionId;
	private String numero;
	private int piso;
	private boolean llegadaHoy; // tiene una llegada hoy: se limpia primero

	public String getHabitacionId() {
		return habitacionId;
	}

	public void setHabitacionId(String habitacionId) {
		this.habitacionId = habitacionId;
	}
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	public int getPiso() {
		return piso;
	}

	public void setPiso(int piso) {
		this.piso = piso;
	}
	public boolean isLlegadaHoy() {
		return llegadaHoy;
	}

	public void setLlegadaHoy(boolean llegadaHoy) {
		this.llegadaHoy = llegadaHoy;
	}
}
