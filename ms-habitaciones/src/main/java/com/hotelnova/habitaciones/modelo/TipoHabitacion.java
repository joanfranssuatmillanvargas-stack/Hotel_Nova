package com.hotelnova.habitaciones.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Tipo de habitación con su capacidad y tarifa base por noche. */
@Document(collection = "tipos_habitacion")
public class TipoHabitacion {

	@Id
	private String id;

	private String nombre; // sencilla, doble, suite... (único)
	private String descripcion;
	private int capacidad; // número máximo de personas
	private double tarifaBase; // valor por noche

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
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}
	public double getTarifaBase() {
		return tarifaBase;
	}

	public void setTarifaBase(double tarifaBase) {
		this.tarifaBase = tarifaBase;
	}
}
