package com.hotelnova.servicios.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Factura de una estadía: alojamiento más consumos, con IVA. */
@Document(collection = "facturas")
public class Factura {

	@Id
	private String id;

	private long numero; // consecutivo
	private String reservaId; // referencia a ms-reservas
	private String huespedDocumento;
	private String numeroHabitacion;
	private long noches;
	private LocalDateTime fecha;
	private double valorAlojamiento;
	private List<LineaFactura> lineas = new ArrayList<>(); // embebidas
	private double subtotal;
	private double porcentajeIva;
	private double iva;
	private double total;
	private EstadoFactura estado;
	private Pago pago; // embebido; null mientras está pendiente

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public long getNumero() {
		return numero;
	}

	public void setNumero(long numero) {
		this.numero = numero;
	}
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
	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
	public double getValorAlojamiento() {
		return valorAlojamiento;
	}

	public void setValorAlojamiento(double valorAlojamiento) {
		this.valorAlojamiento = valorAlojamiento;
	}
	public List<LineaFactura> getLineas() {
		return lineas;
	}

	public void setLineas(List<LineaFactura> lineas) {
		this.lineas = lineas;
	}
	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	public double getPorcentajeIva() {
		return porcentajeIva;
	}

	public void setPorcentajeIva(double porcentajeIva) {
		this.porcentajeIva = porcentajeIva;
	}
	public double getIva() {
		return iva;
	}

	public void setIva(double iva) {
		this.iva = iva;
	}
	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}
	public EstadoFactura getEstado() {
		return estado;
	}

	public void setEstado(EstadoFactura estado) {
		this.estado = estado;
	}
	public Pago getPago() {
		return pago;
	}

	public void setPago(Pago pago) {
		this.pago = pago;
	}
}
