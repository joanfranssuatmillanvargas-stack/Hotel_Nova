package com.hotelnova.servicios.modelo;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/** Ingresos por alojamiento y por categoría de servicio en un periodo. */
public class ReporteIngresos {

	private LocalDate desde;
	private LocalDate hasta;
	private long facturas;
	private double alojamiento;
	private Map<String, Double> porCategoria = new LinkedHashMap<>();
	private double subtotal;
	private double iva;
	private double total;
	private double pagado;

	public LocalDate getDesde() {
		return desde;
	}

	public void setDesde(LocalDate desde) {
		this.desde = desde;
	}
	public LocalDate getHasta() {
		return hasta;
	}

	public void setHasta(LocalDate hasta) {
		this.hasta = hasta;
	}
	public long getFacturas() {
		return facturas;
	}

	public void setFacturas(long facturas) {
		this.facturas = facturas;
	}
	public double getAlojamiento() {
		return alojamiento;
	}

	public void setAlojamiento(double alojamiento) {
		this.alojamiento = alojamiento;
	}
	public Map<String, Double> getPorCategoria() {
		return porCategoria;
	}

	public void setPorCategoria(Map<String, Double> porCategoria) {
		this.porCategoria = porCategoria;
	}
	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
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
	public double getPagado() {
		return pagado;
	}

	public void setPagado(double pagado) {
		this.pagado = pagado;
	}
}
