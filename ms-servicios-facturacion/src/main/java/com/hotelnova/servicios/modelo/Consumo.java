package com.hotelnova.servicios.modelo;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Consumo de un servicio cargado a la estadía (reserva) de un huésped. */
@Document(collection = "consumos")
public class Consumo {

	@Id
	private String id;

	private String reservaId; // referencia a ms-reservas
	private String servicioId; // referencia a Servicio
	private String nombreServicio; // copia para la factura
	private Categoria categoria;
	private int cantidad;
	private double precioUnitario; // precio al momento del consumo
	private double subtotal;
	private String empleado;
	private LocalDateTime fecha;
	private boolean anulado;
	private String motivoAnulacion;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getReservaId() {
		return reservaId;
	}

	public void setReservaId(String reservaId) {
		this.reservaId = reservaId;
	}
	public String getServicioId() {
		return servicioId;
	}

	public void setServicioId(String servicioId) {
		this.servicioId = servicioId;
	}
	public String getNombreServicio() {
		return nombreServicio;
	}

	public void setNombreServicio(String nombreServicio) {
		this.nombreServicio = nombreServicio;
	}
	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
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
	public boolean isAnulado() {
		return anulado;
	}

	public void setAnulado(boolean anulado) {
		this.anulado = anulado;
	}
	public String getMotivoAnulacion() {
		return motivoAnulacion;
	}

	public void setMotivoAnulacion(String motivoAnulacion) {
		this.motivoAnulacion = motivoAnulacion;
	}
}
