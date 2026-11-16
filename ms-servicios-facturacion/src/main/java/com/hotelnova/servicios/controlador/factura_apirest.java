package com.hotelnova.servicios.controlador;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelnova.servicios.modelo.CierreCaja;
import com.hotelnova.servicios.modelo.EstadoFactura;
import com.hotelnova.servicios.modelo.Factura;
import com.hotelnova.servicios.modelo.MedioPago;
import com.hotelnova.servicios.modelo.ReporteIngresos;
import com.hotelnova.servicios.modelo.SolicitudFactura;
import com.hotelnova.servicios.repositorio.factura_repositorio;
import com.hotelnova.servicios.repositorio.reglas_repositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Facturas", description = "Factura de cada estadía (alojamiento + consumos + IVA), pagos del cajero, cierre de caja y reporte de ingresos")
@RestController
@RequestMapping("/api/facturas")
public class factura_apirest {

	private final factura_repositorio facturas;
	private final reglas_repositorio reglas;

	public factura_apirest(factura_repositorio facturas, reglas_repositorio reglas) {
		this.facturas = facturas;
		this.reglas = reglas;
	}

	@Operation(summary = "Listar facturas (opcional: la de una reserva, o solo las PENDIENTES para la bandeja del cajero, SER-11)")
	@GetMapping
	public List<Factura> listar(@RequestParam(name = "reservaId", required = false) String reservaId,
			@RequestParam(name = "estado", required = false) EstadoFactura estado) {
		if (reservaId != null && !reservaId.isBlank()) {
			return facturas.findByReservaId(reservaId).map(List::of).orElse(List.of());
		}
		if (estado == EstadoFactura.PENDIENTE) {
			return reglas.pendientes();
		}
		if (estado != null) {
			return facturas.findByEstadoOrderByNumeroAsc(estado);
		}
		return facturas.findAllByOrderByNumeroDesc();
	}

	@Operation(summary = "Buscar factura por id")
	@GetMapping("/{id}")
	public Factura buscar(@PathVariable("id") String id) {
		return reglas.factura(id);
	}

	@Operation(summary = "Generar factura de una estadía (SER-06, SER-07). La llama ms-reservas en el check-out")
	@PostMapping("/generar")
	public Factura generar(@RequestBody SolicitudFactura solicitud) {
		return reglas.generar(solicitud);
	}

	@Operation(summary = "Registrar pago (SER-08, SER-11 · REQ-HN-009, 010): medio EFECTIVO, TARJETA o TRANSFERENCIA y el cajero que lo recibe. "
			+ "400 si falta el medio o el cajero, 404 si no existe la factura, 409 si ya está pagada")
	@PostMapping("/{id}/pago")
	public Factura pagar(@PathVariable("id") String id, @RequestParam("medio") MedioPago medio,
			@RequestParam(name = "cajero", required = false) String cajero) {
		return reglas.pagar(id, medio, cajero);
	}

	@Operation(summary = "Cierre de caja de un día y un cajero (SER-12 · REQ-HN-012): total por medio de pago y número de facturas pagadas. "
			+ "Un día sin pagos devuelve todo en 0")
	@GetMapping("/cierre")
	public CierreCaja cierre(@RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
			@RequestParam("cajero") String cajero) {
		return reglas.cierre(fecha, cajero);
	}

	@Operation(summary = "Reporte de ingresos por alojamiento y por categoría de servicio (SER-10)")
	@GetMapping("/reporte")
	public ReporteIngresos reporte(
			@RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
		return reglas.reporte(desde, hasta);
	}
}
