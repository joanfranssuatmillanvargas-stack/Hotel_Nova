package com.hotelnova.reservas.modelo;



/** Habitación libre para un rango de fechas, con el valor de la estadía. */
public class Disponibilidad {

	private String habitacionId;
	private String numero;
	private int piso;
	private String tipoId;
	private String tipoNombre;
	private int capacidad;
	private long noches;
	private double valorEstadia;

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
	public String getTipoId() {
		return tipoId;
	}

	public void setTipoId(String tipoId) {
		this.tipoId = tipoId;
	}
	public String getTipoNombre() {
		return tipoNombre;
	}

	public void setTipoNombre(String tipoNombre) {
		this.tipoNombre = tipoNombre;
	}
	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}
	public long getNoches() {
		return noches;
	}

	public void setNoches(long noches) {
		this.noches = noches;
	}
	public double getValorEstadia() {
		return valorEstadia;
	}

	public void setValorEstadia(double valorEstadia) {
		this.valorEstadia = valorEstadia;
	}
}
