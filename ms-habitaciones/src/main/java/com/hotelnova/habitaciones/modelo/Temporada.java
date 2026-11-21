package com.hotelnova.habitaciones.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Tarifa especial de un tipo de habitación en un rango de fechas. */
@Document(collection = "temporadas")
public class Temporada {

	@Id
	private String id;

	private String nombre; // alta, baja...
	private LocalDate inicio;
	private LocalDate fin;
	private String tipoId; // referencia a TipoHabitacion
	private double tarifa; // valor por noche en la temporada
	private EstadoTemporada estado; // HAB-20; null = guardada antes del R2-Sprint 3 (vigente, ADR-008)
	private String revisadaPor; // gerente que la aprobó o rechazó
	private LocalDateTime revisadaEn;

	/** ADR-008: cambia la tarifa solo si está aprobada (o si viene del Release 1, sin estado). */
	public boolean vigente() {
		return estado == null || estado == EstadoTemporada.APROBADA;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public LocalDate getInicio() {
		return inicio;
	}

	public void setInicio(LocalDate inicio) {
		this.inicio = inicio;
	}
	public LocalDate getFin() {
		return fin;
	}

	public void setFin(LocalDate fin) {
		this.fin = fin;
	}
	public String getTipoId() {
		return tipoId;
	}

	public void setTipoId(String tipoId) {
		this.tipoId = tipoId;
	}
	public double getTarifa() {
		return tarifa;
	}

	public void setTarifa(double tarifa) {
		this.tarifa = tarifa;
	}

	public EstadoTemporada getEstado() {
		return estado;
	}

	public void setEstado(EstadoTemporada estado) {
		this.estado = estado;
	}
	public String getRevisadaPor() {
		return revisadaPor;
	}

	public void setRevisadaPor(String revisadaPor) {
		this.revisadaPor = revisadaPor;
	}
	public LocalDateTime getRevisadaEn() {
		return revisadaEn;
	}

	public void setRevisadaEn(LocalDateTime revisadaEn) {
		this.revisadaEn = revisadaEn;
	}
}
