package com.hotelnova.servicios.repositorio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.servicios.modelo.Categoria;
import com.hotelnova.servicios.modelo.Consumo;
import com.hotelnova.servicios.modelo.CuentaEstadia;
import com.hotelnova.servicios.modelo.EstadoFactura;
import com.hotelnova.servicios.modelo.Factura;
import com.hotelnova.servicios.modelo.LineaFactura;
import com.hotelnova.servicios.modelo.MedioPago;
import com.hotelnova.servicios.modelo.Pago;
import com.hotelnova.servicios.modelo.ReporteIngresos;
import com.hotelnova.servicios.modelo.Servicio;
import com.hotelnova.servicios.modelo.SolicitudFactura;

/**
 * Reglas de negocio de servicios adicionales y facturación.
 * Los errores se lanzan como ResponseStatusException con su código HTTP.
 */
@Repository
public class reglas_repositorio {

	private final servicio_repositorio servicios;
	private final consumo_repositorio consumos;
	private final factura_repositorio facturas;
	private final contador_repositorio contadores;
	private final externos_repositorio externos;
	private final ZoneId zona;
	private final double porcentajeIva;

	public reglas_repositorio(servicio_repositorio servicios, consumo_repositorio consumos, factura_repositorio facturas,
			contador_repositorio contadores, externos_repositorio externos, @Value("${hotelnova.zona}") String zona,
			@Value("${hotelnova.iva}") double porcentajeIva) {
		this.servicios = servicios;
		this.consumos = consumos;
		this.facturas = facturas;
		this.contadores = contadores;
		this.externos = externos;
		this.zona = ZoneId.of(zona);
		this.porcentajeIva = porcentajeIva;
	}

	// ======================= Catálogo (SER-01 a SER-03) =======================

	public Servicio servicio(String id) {
		return servicios.findById(id).orElseThrow(() -> noEncontrado("No existe un servicio con id " + id));
	}

	public List<Servicio> catalogo(Categoria categoria, boolean soloActivos) {
		return servicios.findAllByOrderByCategoriaAscNombreAsc().stream()
				.filter(s -> categoria == null || s.getCategoria() == categoria)
				.filter(s -> !soloActivos || s.isActivo())
				.toList();
	}

	public Servicio guardarServicio(String id, Servicio datos) {
		if (datos.getNombre() == null || datos.getNombre().isBlank()) {
			throw invalido("Falta el nombre del servicio");
		}
		if (datos.getCategoria() == null) {
			throw invalido("La categoría debe ser RESTAURANTE, LAVANDERIA, PARQUEADERO o SPA");
		}
		if (datos.getPrecio() <= 0) {
			throw invalido("El precio debe ser mayor que 0");
		}
		if (id == null) {
			datos.setActivo(true);
		} else {
			datos.setActivo(servicio(id).isActivo());
		}
		datos.setId(id);
		datos.setNombre(datos.getNombre().trim());
		return servicios.save(datos);
	}

	public Servicio cambiarActivo(String id, boolean activo) {
		Servicio s = servicio(id);
		s.setActivo(activo);
		return servicios.save(s);
	}

	public void eliminarServicio(String id) {
		Servicio s = servicio(id);
		if (consumos.existsByServicioId(id)) {
			throw conflicto("No se puede eliminar " + s.getNombre() + ": ya tiene consumos registrados. Desactívalo en su lugar.");
		}
		servicios.delete(s);
	}

	// ======================= Consumos (SER-04, SER-05, SER-09) =======================

	public Consumo registrarConsumo(Consumo datos) {
		if (datos.getReservaId() == null || datos.getReservaId().isBlank()) {
			throw invalido("Falta la estadía (reserva) a la que se carga el consumo");
		}
		if (datos.getCantidad() < 1) {
			throw invalido("La cantidad debe ser al menos 1");
		}
		Servicio s = servicio(datos.getServicioId() == null ? "" : datos.getServicioId());
		if (!s.isActivo()) {
			throw conflicto("El servicio " + s.getNombre() + " está desactivado");
		}
		Map<String, Object> reserva = externos.reserva(datos.getReservaId());
		if (!"EN_CURSO".equals(reserva.get("estado"))) {
			throw conflicto("Solo se cargan consumos a estadías con check-in realizado; la reserva " + reserva.get("codigo")
					+ " está " + reserva.get("estado"));
		}
		Consumo c = new Consumo();
		c.setReservaId(datos.getReservaId());
		c.setServicioId(s.getId());
		c.setNombreServicio(s.getNombre());
		c.setCategoria(s.getCategoria());
		c.setCantidad(datos.getCantidad());
		c.setPrecioUnitario(s.getPrecio());
		c.setSubtotal(s.getPrecio() * datos.getCantidad());
		c.setEmpleado(datos.getEmpleado());
		c.setFecha(ahora());
		return consumos.save(c);
	}

	public CuentaEstadia cuenta(String reservaId) {
		CuentaEstadia cuenta = new CuentaEstadia();
		cuenta.setReservaId(reservaId);
		List<Consumo> lista = consumos.findByReservaIdOrderByFechaAsc(reservaId);
		cuenta.setConsumos(lista);
		cuenta.setTotal(lista.stream().filter(c -> !c.isAnulado()).mapToDouble(Consumo::getSubtotal).sum());
		return cuenta;
	}

	public Consumo anularConsumo(String id, String motivo) {
		Consumo c = consumos.findById(id).orElseThrow(() -> noEncontrado("No existe un consumo con id " + id));
		if (motivo == null || motivo.isBlank()) {
			throw invalido("Indica el motivo de la anulación");
		}
		if (c.isAnulado()) {
			throw conflicto("El consumo ya estaba anulado");
		}
		if (facturas.existsByReservaId(c.getReservaId())) {
			throw conflicto("La estadía ya fue facturada (check-out hecho): no se pueden anular sus consumos");
		}
		c.setAnulado(true);
		c.setMotivoAnulacion(motivo.trim());
		return consumos.save(c);
	}

	// ======================= Facturas (SER-06 a SER-08) =======================

	public Factura factura(String id) {
		return facturas.findById(id).orElseThrow(() -> noEncontrado("No existe una factura con id " + id));
	}

	/** SER-06 / SER-07: alojamiento + consumos no anulados, con IVA. Si ya existe, la devuelve. */
	public Factura generar(SolicitudFactura s) {
		if (s.getReservaId() == null || s.getReservaId().isBlank()) {
			throw invalido("Falta la reserva a facturar");
		}
		var existente = facturas.findByReservaId(s.getReservaId());
		if (existente.isPresent()) {
			return existente.get();
		}
		List<LineaFactura> lineas = new ArrayList<>();
		lineas.add(linea("Alojamiento habitación " + s.getNumeroHabitacion() + " (" + s.getNoches() + " noche(s))",
				"ALOJAMIENTO", (int) s.getNoches(), s.getNoches() == 0 ? 0 : s.getValorAlojamiento() / s.getNoches(),
				s.getValorAlojamiento()));
		for (Consumo c : consumos.findByReservaIdOrderByFechaAsc(s.getReservaId())) {
			if (!c.isAnulado()) {
				lineas.add(linea(c.getNombreServicio(), c.getCategoria().name(), c.getCantidad(), c.getPrecioUnitario(),
						c.getSubtotal()));
			}
		}
		double subtotal = lineas.stream().mapToDouble(LineaFactura::getValor).sum();
		double iva = redondear(subtotal * porcentajeIva);
		Factura f = new Factura();
		f.setNumero(contadores.siguiente("facturas"));
		f.setReservaId(s.getReservaId());
		f.setHuespedDocumento(s.getHuespedDocumento());
		f.setNumeroHabitacion(s.getNumeroHabitacion());
		f.setNoches(s.getNoches());
		f.setFecha(ahora());
		f.setValorAlojamiento(s.getValorAlojamiento());
		f.setLineas(lineas);
		f.setSubtotal(redondear(subtotal));
		f.setPorcentajeIva(porcentajeIva);
		f.setIva(iva);
		f.setTotal(redondear(subtotal + iva));
		f.setEstado(EstadoFactura.PENDIENTE);
		return facturas.save(f);
	}

	public Factura pagar(String id, MedioPago medio) {
		if (medio == null) {
			throw invalido("El medio de pago debe ser EFECTIVO, TARJETA o TRANSFERENCIA");
		}
		Factura f = factura(id);
		if (f.getEstado() == EstadoFactura.PAGADA) {
			throw conflicto("La factura No. " + f.getNumero() + " ya está pagada");
		}
		Pago p = new Pago();
		p.setMedio(medio);
		p.setFecha(ahora());
		p.setValor(f.getTotal());
		f.setPago(p);
		f.setEstado(EstadoFactura.PAGADA);
		return facturas.save(f);
	}

	// ======================= Reporte (SER-10) =======================

	public ReporteIngresos reporte(LocalDate desde, LocalDate hasta) {
		if (desde == null || hasta == null || hasta.isBefore(desde)) {
			throw invalido("Indica un rango de fechas válido (desde y hasta)");
		}
		List<Factura> lista = facturas.findAll().stream()
				.filter(f -> !f.getFecha().toLocalDate().isBefore(desde) && !f.getFecha().toLocalDate().isAfter(hasta))
				.toList();
		Map<String, Double> porCategoria = new LinkedHashMap<>();
		for (Categoria c : Categoria.values()) {
			porCategoria.put(c.name(), 0.0);
		}
		double alojamiento = 0;
		for (Factura f : lista) {
			for (LineaFactura l : f.getLineas()) {
				if ("ALOJAMIENTO".equals(l.getCategoria())) {
					alojamiento += l.getValor();
				} else {
					porCategoria.merge(l.getCategoria(), l.getValor(), Double::sum);
				}
			}
		}
		ReporteIngresos r = new ReporteIngresos();
		r.setDesde(desde);
		r.setHasta(hasta);
		r.setFacturas(lista.size());
		r.setAlojamiento(redondear(alojamiento));
		r.setPorCategoria(porCategoria);
		r.setSubtotal(redondear(lista.stream().mapToDouble(Factura::getSubtotal).sum()));
		r.setIva(redondear(lista.stream().mapToDouble(Factura::getIva).sum()));
		r.setTotal(redondear(lista.stream().mapToDouble(Factura::getTotal).sum()));
		r.setPagado(redondear(lista.stream().filter(f -> f.getEstado() == EstadoFactura.PAGADA)
				.mapToDouble(Factura::getTotal).sum()));
		return r;
	}

	// ======================= Ayudas =======================

	private static LineaFactura linea(String concepto, String categoria, int cantidad, double unitario, double valor) {
		LineaFactura l = new LineaFactura();
		l.setConcepto(concepto);
		l.setCategoria(categoria);
		l.setCantidad(cantidad);
		l.setValorUnitario(redondear(unitario));
		l.setValor(redondear(valor));
		return l;
	}

	private static double redondear(double v) {
		return Math.round(v * 100.0) / 100.0;
	}

	private LocalDateTime ahora() {
		return LocalDateTime.now(zona).withNano(0);
	}

	private ResponseStatusException noEncontrado(String mensaje) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, mensaje);
	}

	private ResponseStatusException conflicto(String mensaje) {
		return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
	}

	private ResponseStatusException invalido(String mensaje) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
	}
}
