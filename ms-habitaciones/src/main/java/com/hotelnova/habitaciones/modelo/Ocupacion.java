package com.hotelnova.habitaciones.modelo;

import java.util.LinkedHashMap;
import java.util.Map;

/** Tablero de ocupación del hotel. */
public class Ocupacion {

	private long total;
	private Map<String, Long> porEstado = new LinkedHashMap<>();
	private double porcentajeOcupacion;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}
	public Map<String, Long> getPorEstado() {
		return porEstado;
	}

	public void setPorEstado(Map<String, Long> porEstado) {
		this.porEstado = porEstado;
	}
	public double getPorcentajeOcupacion() {
		return porcentajeOcupacion;
	}

	public void setPorcentajeOcupacion(double porcentajeOcupacion) {
		this.porcentajeOcupacion = porcentajeOcupacion;
	}
}
