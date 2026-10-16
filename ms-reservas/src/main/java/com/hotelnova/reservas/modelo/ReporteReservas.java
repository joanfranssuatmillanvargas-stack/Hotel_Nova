package com.hotelnova.reservas.modelo;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/** Reporte de reservas por rango de fechas de llegada. */
public class ReporteReservas {

	private LocalDate desde;
	private LocalDate hasta;
	private long total;
	private Map<String, Long> porEstado = new LinkedHashMap<>();
	private double valorAlojamiento; // reservas no canceladas

	public LocalDate getDesde() {
		return desde;
	}

	public void setDesde(LocalDate desde) {
		this.desde = desde;
	}
	public LocalDate getHasta() {
		return hasta;
	}

	public void setHasta(LocalDate hasta) {
		this.hasta = hasta;
	}
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
	public double getValorAlojamiento() {
		return valorAlojamiento;
	}

	public void setValorAlojamiento(double valorAlojamiento) {
		this.valorAlojamiento = valorAlojamiento;
	}
}
