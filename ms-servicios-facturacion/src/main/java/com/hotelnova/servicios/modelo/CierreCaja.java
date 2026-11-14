package com.hotelnova.servicios.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cierre de caja de un día y un cajero (SER-12, REQ-HN-012). No se guarda: se calcula con los pagos del día (ADR-006).
 */
public class CierreCaja {

	private LocalDate fecha;
	private String cajero;
	private double efectivo;
	private double tarjeta;
	private double transferencia;
	private double total;
	private int facturas; // número de facturas pagadas ese día por ese cajero
	private List<Factura> detalle = new ArrayList<>();

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getCajero() {
		return cajero;
	}

	public void setCajero(String cajero) {
		this.cajero = cajero;
	}

	public double getEfectivo() {
		return efectivo;
	}

	public void setEfectivo(double efectivo) {
		this.efectivo = efectivo;
	}

	public double getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(double tarjeta) {
		this.tarjeta = tarjeta;
	}

	public double getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(double transferencia) {
		this.transferencia = transferencia;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public int getFacturas() {
		return facturas;
	}

	public void setFacturas(int facturas) {
		this.facturas = facturas;
	}

	public List<Factura> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<Factura> detalle) {
		this.detalle = detalle;
	}
}
