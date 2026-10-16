package com.hotelnova.habitaciones.modelo;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Habitación del hotel. */
@Document(collection = "habitaciones")
public class Habitacion {

	@Id
	private String id;

	private String numero; // único
	private int piso;
	private String tipoId; // referencia a TipoHabitacion
	private EstadoHabitacion estado;
	private String actualizadoPor; // último usuario que cambió el estado
	private LocalDateTime actualizadoEn;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	public EstadoHabitacion getEstado() {
		return estado;
	}

	public void setEstado(EstadoHabitacion estado) {
		this.estado = estado;
	}
	public String getActualizadoPor() {
		return actualizadoPor;
	}

	public void setActualizadoPor(String actualizadoPor) {
		this.actualizadoPor = actualizadoPor;
	}
	public LocalDateTime getActualizadoEn() {
		return actualizadoEn;
	}

	public void setActualizadoEn(LocalDateTime actualizadoEn) {
		this.actualizadoEn = actualizadoEn;
	}
}
