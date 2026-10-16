package com.hotelnova.habitaciones.modelo;

import java.time.LocalDate;

/** Valor del alojamiento de una estadía, noche por noche y con tarifas de temporada. */
public class ValorEstadia {

	private String habitacionId;
	private LocalDate llegada;
	private LocalDate salida;
	private long noches;
	private double valor; // suma de la tarifa de cada noche

	public String getHabitacionId() {
		return habitacionId;
	}

	public void setHabitacionId(String habitacionId) {
		this.habitacionId = habitacionId;
	}
	public LocalDate getLlegada() {
		return llegada;
	}

	public void setLlegada(LocalDate llegada) {
		this.llegada = llegada;
	}
	public LocalDate getSalida() {
		return salida;
	}

	public void setSalida(LocalDate salida) {
		this.salida = salida;
	}
	public long getNoches() {
		return noches;
	}

	public void setNoches(long noches) {
		this.noches = noches;
	}
	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
}
