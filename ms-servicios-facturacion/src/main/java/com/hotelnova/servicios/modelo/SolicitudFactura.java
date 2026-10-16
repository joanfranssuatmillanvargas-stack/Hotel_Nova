package com.hotelnova.servicios.modelo;



/** Lo envía ms-reservas en el check-out para generar la factura. */
public class SolicitudFactura {

	private String reservaId;
	private String huespedDocumento;
	private String numeroHabitacion;
	private long noches;
	private double valorAlojamiento;

	public String getReservaId() {
		return reservaId;
	}

	public void setReservaId(String reservaId) {
		this.reservaId = reservaId;
	}
	public String getHuespedDocumento() {
		return huespedDocumento;
	}

	public void setHuespedDocumento(String huespedDocumento) {
		this.huespedDocumento = huespedDocumento;
	}
	public String getNumeroHabitacion() {
		return numeroHabitacion;
	}

	public void setNumeroHabitacion(String numeroHabitacion) {
		this.numeroHabitacion = numeroHabitacion;
	}
	public long getNoches() {
		return noches;
	}

	public void setNoches(long noches) {
		this.noches = noches;
	}
	public double getValorAlojamiento() {
		return valorAlojamiento;
	}

	public void setValorAlojamiento(double valorAlojamiento) {
		this.valorAlojamiento = valorAlojamiento;
	}
}
