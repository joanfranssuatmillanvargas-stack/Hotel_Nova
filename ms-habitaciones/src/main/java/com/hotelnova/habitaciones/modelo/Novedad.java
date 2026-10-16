package com.hotelnova.habitaciones.modelo;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Registro del personal de limpieza: limpieza realizada o daño reportado. */
@Document(collection = "novedades")
public class Novedad {

	@Id
	private String id;

	private String habitacionId; // referencia a Habitacion
	private String numeroHabitacion;
	private TipoNovedad tipo;
	private String descripcion;
	private String empleado;
	private LocalDateTime fecha;
	private boolean pendiente; // daño reportado con la habitación ocupada: se atiende al limpiarla

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getHabitacionId() {
		return habitacionId;
	}

	public void setHabitacionId(String habitacionId) {
		this.habitacionId = habitacionId;
	}
	public String getNumeroHabitacion() {
		return numeroHabitacion;
	}

	public void setNumeroHabitacion(String numeroHabitacion) {
		this.numeroHabitacion = numeroHabitacion;
	}
	public TipoNovedad getTipo() {
		return tipo;
	}

	public void setTipo(TipoNovedad tipo) {
		this.tipo = tipo;
	}
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getEmpleado() {
		return empleado;
	}

	public void setEmpleado(String empleado) {
		this.empleado = empleado;
	}
	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
	public boolean isPendiente() {
		return pendiente;
	}

	public void setPendiente(boolean pendiente) {
		this.pendiente = pendiente;
	}
}
