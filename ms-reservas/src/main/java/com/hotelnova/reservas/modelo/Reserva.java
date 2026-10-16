package com.hotelnova.reservas.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Reserva de una habitación. Huésped y habitación se guardan por su identificador. */
@Document(collection = "reservas")
public class Reserva {

	@Id
	private String id;

	private String codigo; // consecutivo R-0001, R-0002...
	private String huespedDocumento; // referencia a ms-huespedes
	private String nombreHuesped; // copia para mostrar
	private String habitacionId; // referencia a ms-habitaciones
	private String numeroHabitacion; // copia para mostrar
	private String tipoId; // tipo de la habitación
	private LocalDate llegada;
	private LocalDate salida;
	private long noches;
	private EstadoReserva estado;
	private double valorAlojamiento; // calculado con las tarifas de ms-habitaciones
	private LocalDateTime creadaEn;
	private LocalDateTime checkIn;
	private LocalDateTime checkOut;
	private String facturaId; // factura generada en el check-out

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getHuespedDocumento() {
		return huespedDocumento;
	}

	public void setHuespedDocumento(String huespedDocumento) {
		this.huespedDocumento = huespedDocumento;
	}
	public String getNombreHuesped() {
		return nombreHuesped;
	}

	public void setNombreHuesped(String nombreHuesped) {
		this.nombreHuesped = nombreHuesped;
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
	public String getTipoId() {
		return tipoId;
	}

	public void setTipoId(String tipoId) {
		this.tipoId = tipoId;
	}
	public LocalDate getLlegada() {
		return llegada;
	}

	public void setLlegada(LocalDate llegada) {
		this.llegada = llegada;
	}
	public LocalDate getSalida() {
		return salida;
	}

	public void setSalida(LocalDate salida) {
		this.salida = salida;
	}
	public long getNoches() {
		return noches;
	}

	public void setNoches(long noches) {
		this.noches = noches;
	}
	public EstadoReserva getEstado() {
		return estado;
	}

	public void setEstado(EstadoReserva estado) {
		this.estado = estado;
	}
	public double getValorAlojamiento() {
		return valorAlojamiento;
	}

	public void setValorAlojamiento(double valorAlojamiento) {
		this.valorAlojamiento = valorAlojamiento;
	}
	public LocalDateTime getCreadaEn() {
		return creadaEn;
	}

	public void setCreadaEn(LocalDateTime creadaEn) {
		this.creadaEn = creadaEn;
	}
	public LocalDateTime getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(LocalDateTime checkIn) {
		this.checkIn = checkIn;
	}
	public LocalDateTime getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(LocalDateTime checkOut) {
		this.checkOut = checkOut;
	}
	public String getFacturaId() {
		return facturaId;
	}

	public void setFacturaId(String facturaId) {
		this.facturaId = facturaId;
	}
}
