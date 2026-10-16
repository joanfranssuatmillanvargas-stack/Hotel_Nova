package com.hotelnova.servicios.modelo;

import java.util.ArrayList;
import java.util.List;

/** Consumos de una estadía con el total acumulado. */
public class CuentaEstadia {

	private String reservaId;
	private List<Consumo> consumos = new ArrayList<>();
	private double total;

	public String getReservaId() {
		return reservaId;
	}

	public void setReservaId(String reservaId) {
		this.reservaId = reservaId;
	}
	public List<Consumo> getConsumos() {
		return consumos;
	}

	public void setConsumos(List<Consumo> consumos) {
		this.consumos = consumos;
	}
	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}
}
