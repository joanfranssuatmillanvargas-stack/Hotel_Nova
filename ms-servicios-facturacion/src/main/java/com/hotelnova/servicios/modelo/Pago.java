package com.hotelnova.servicios.modelo;

import java.time.LocalDateTime;

/** Pago de la factura (embebido). */
public class Pago {

	private MedioPago medio;
	private LocalDateTime fecha;
	private double valor;

	public MedioPago getMedio() {
		return medio;
	}

	public void setMedio(MedioPago medio) {
		this.medio = medio;
	}
	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
}
