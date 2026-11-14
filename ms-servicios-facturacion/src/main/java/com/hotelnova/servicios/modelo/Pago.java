package com.hotelnova.servicios.modelo;

import java.time.LocalDateTime;

/** Pago de la factura (embebido). Desde el R2-Sprint 2 guarda qué cajero lo recibió (SER-11, REQ-HN-009). */
public class Pago {

	private MedioPago medio;
	private LocalDateTime fecha;
	private double valor;
	private String cajero; // usuario del cajero que recibió el pago (null en los pagos del Release 1)

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

	public String getCajero() {
		return cajero;
	}

	public void setCajero(String cajero) {
		this.cajero = cajero;
	}
}
