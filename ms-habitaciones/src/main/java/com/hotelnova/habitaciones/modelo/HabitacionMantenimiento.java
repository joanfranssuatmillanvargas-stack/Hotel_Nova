package com.hotelnova.habitaciones.modelo;

import java.time.LocalDateTime;

/** Habitación en MANTENIMIENTO con su último daño reportado (HAB-16, REQ-HN-006). */
public class HabitacionMantenimiento {

	private String habitacionId;
	private String numero;
	private int piso;
	private String dano; // descripción del daño más reciente
	private String reportadoPor; // quién reportó el daño
	private LocalDateTime fechaReporte;

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

	public String getDano() {
		return dano;
	}

	public void setDano(String dano) {
		this.dano = dano;
	}

	public String getReportadoPor() {
		return reportadoPor;
	}

	public void setReportadoPor(String reportadoPor) {
		this.reportadoPor = reportadoPor;
	}

	public LocalDateTime getFechaReporte() {
		return fechaReporte;
	}

	public void setFechaReporte(LocalDateTime fechaReporte) {
		this.fechaReporte = fechaReporte;
	}
}
