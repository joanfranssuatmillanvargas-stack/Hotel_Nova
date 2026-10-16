package com.hotelnova.reservas.repositorio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.reservas.modelo.Disponibilidad;
import com.hotelnova.reservas.modelo.EstadoReserva;
import com.hotelnova.reservas.modelo.ReporteReservas;
import com.hotelnova.reservas.modelo.Reserva;

/**
 * Reglas de negocio de las reservas. Combina la base de datos propia (reservasdb)
 * con consultas REST a ms-habitaciones, ms-huespedes y ms-servicios-facturacion.
 * Los errores se lanzan como ResponseStatusException con su código HTTP.
 */
@Repository
public class reglas_repositorio {

	private static final Set<EstadoReserva> ACTIVAS = EnumSet.of(EstadoReserva.CONFIRMADA, EstadoReserva.EN_CURSO);

	private final reserva_repositorio reservas;
	private final contador_repositorio contadores;
	private final externos_repositorio externos;
	private final ZoneId zona;

	public reglas_repositorio(reserva_repositorio reservas, contador_repositorio contadores, externos_repositorio externos,
			@Value("${hotelnova.zona}") String zona) {
		this.reservas = reservas;
		this.contadores = contadores;
		this.externos = externos;
		this.zona = ZoneId.of(zona);
	}

	public Reserva reserva(String id) {
		return reservas.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe una reserva con id " + id));
	}

	/** RES-02: consulta con filtros combinables, ordenada por fecha de llegada. */
	public List<Reserva> listar(String huespedDocumento, EstadoReserva estado, String habitacionId, LocalDate desde,
			LocalDate hasta) {
		return reservas.findAllByOrderByLlegadaAsc().stream()
				.filter(r -> vacio(huespedDocumento) || huespedDocumento.equals(r.getHuespedDocumento()))
				.filter(r -> estado == null || r.getEstado() == estado)
				.filter(r -> vacio(habitacionId) || habitacionId.equals(r.getHabitacionId()))
				.filter(r -> desde == null || !r.getLlegada().isBefore(desde))
				.filter(r -> hasta == null || !r.getLlegada().isAfter(hasta))
				.toList();
	}

	// ======================= Crear y modificar (RES-01, RES-03, RES-05, RES-09) =======================

	public Reserva crear(Reserva datos) {
		obligatorio(datos.getHuespedDocumento(), "el documento del huésped");
		obligatorio(datos.getHabitacionId(), "la habitación");
		validarFechas(datos.getLlegada(), datos.getSalida());
		if (datos.getLlegada().isBefore(hoy())) {
			throw invalido("La fecha de llegada no puede ser anterior a hoy");
		}
		Map<String, Object> huesped = externos.huesped(datos.getHuespedDocumento().trim());
		Reserva r = new Reserva();
		r.setHuespedDocumento(datos.getHuespedDocumento().trim());
		r.setNombreHuesped(huesped.get("nombres") + " " + huesped.get("apellidos"));
		r.setEstado(EstadoReserva.CONFIRMADA);
		r.setCreadaEn(ahora());
		asignarHabitacionYFechas(r, datos.getHabitacionId(), datos.getLlegada(), datos.getSalida());
		r.setCodigo(String.format("R-%04d", contadores.siguiente("reservas"))); // solo si pasó todas las validaciones
		return reservas.save(r);
	}

	public Reserva modificar(String id, Reserva datos) {
		Reserva r = reserva(id);
		if (r.getEstado() != EstadoReserva.CONFIRMADA) {
			throw conflicto("Solo se pueden modificar reservas confirmadas; la reserva " + r.getCodigo() + " está " + r.getEstado());
		}
		String habitacionId = vacio(datos.getHabitacionId()) ? r.getHabitacionId() : datos.getHabitacionId();
		LocalDate llegada = datos.getLlegada() == null ? r.getLlegada() : datos.getLlegada();
		LocalDate salida = datos.getSalida() == null ? r.getSalida() : datos.getSalida();
		validarFechas(llegada, salida);
		asignarHabitacionYFechas(r, habitacionId, llegada, salida);
		return reservas.save(r);
	}

	/** Valida la habitación, que no haya cruce de fechas y calcula el valor del alojamiento. */
	private void asignarHabitacionYFechas(Reserva r, String habitacionId, LocalDate llegada, LocalDate salida) {
		Map<String, Object> ficha = externos.fichaHabitacion(habitacionId);
		if ("MANTENIMIENTO".equals(ficha.get("estado"))) {
			throw conflicto("La habitación " + ficha.get("numero") + " está en mantenimiento y no se puede reservar");
		}
		Reserva cruce = cruceCon(habitacionId, llegada, salida, r.getId());
		if (cruce != null) {
			throw conflicto("La habitación " + ficha.get("numero") + " ya está reservada del " + cruce.getLlegada() + " al "
					+ cruce.getSalida() + " (reserva " + cruce.getCodigo() + "). Elige otras fechas u otra habitación.");
		}
		Map<String, Object> valor = externos.valorEstadia(habitacionId, llegada, salida);
		r.setHabitacionId(habitacionId);
		r.setNumeroHabitacion(String.valueOf(ficha.get("numero")));
		r.setTipoId(String.valueOf(ficha.get("tipoId")));
		r.setLlegada(llegada);
		r.setSalida(salida);
		r.setNoches(ChronoUnit.DAYS.between(llegada, salida));
		r.setValorAlojamiento(numero(valor.get("valor")));
	}

	/** RES-05: busca una reserva activa de la misma habitación cuyas fechas se crucen. */
	private Reserva cruceCon(String habitacionId, LocalDate llegada, LocalDate salida, String excepto) {
		for (Reserva otra : reservas.findByHabitacionIdAndEstadoIn(habitacionId, ACTIVAS)) {
			boolean seCruzan = llegada.isBefore(otra.getSalida()) && salida.isAfter(otra.getLlegada());
			if (seCruzan && !otra.getId().equals(excepto)) {
				return otra;
			}
		}
		return null;
	}

	/** RES-04: la reserva queda cancelada (no se borra). */
	public Reserva cancelar(String id) {
		Reserva r = reserva(id);
		if (r.getEstado() != EstadoReserva.CONFIRMADA) {
			throw conflicto("Solo se pueden cancelar reservas confirmadas; la reserva " + r.getCodigo() + " está " + r.getEstado());
		}
		r.setEstado(EstadoReserva.CANCELADA);
		return reservas.save(r);
	}

	// ======================= Disponibilidad (RES-06) =======================

	public List<Disponibilidad> disponibilidad(LocalDate llegada, LocalDate salida, String tipoId) {
		validarFechas(llegada, salida);
		List<Disponibilidad> libres = new ArrayList<>();
		for (Map<String, Object> h : externos.habitacionesOfrecibles(tipoId)) {
			String id = String.valueOf(h.get("id"));
			if (cruceCon(id, llegada, salida, null) != null) {
				continue;
			}
			Map<String, Object> valor = externos.valorEstadia(id, llegada, salida);
			Disponibilidad d = new Disponibilidad();
			d.setHabitacionId(id);
			d.setNumero(String.valueOf(h.get("numero")));
			d.setPiso((int) numero(h.get("piso")));
			d.setTipoId(String.valueOf(h.get("tipoId")));
			d.setTipoNombre(String.valueOf(h.get("tipoNombre")));
			d.setCapacidad((int) numero(h.get("capacidad")));
			d.setNoches(ChronoUnit.DAYS.between(llegada, salida));
			d.setValorEstadia(numero(valor.get("valor")));
			libres.add(d);
		}
		libres.sort(Comparator.comparingDouble(Disponibilidad::getValorEstadia).thenComparing(Disponibilidad::getNumero));
		return libres;
	}

	// ======================= Check-in y check-out (RES-07, RES-08) =======================

	public Reserva checkIn(String id, String usuario) {
		Reserva r = reserva(id);
		if (r.getEstado() != EstadoReserva.CONFIRMADA) {
			throw conflicto("Solo se puede hacer check-in de reservas confirmadas; la reserva " + r.getCodigo() + " está " + r.getEstado());
		}
		LocalDate hoy = hoy();
		if (r.getLlegada().isAfter(hoy)) {
			throw conflicto("La reserva " + r.getCodigo() + " llega el " + r.getLlegada() + ": todavía no se puede hacer check-in");
		}
		if (!r.getSalida().isAfter(hoy)) {
			throw conflicto("La fecha de salida de la reserva " + r.getCodigo() + " ya pasó");
		}
		Map<String, Object> ficha = externos.fichaHabitacion(r.getHabitacionId());
		Object estado = ficha.get("estado");
		if (!"DISPONIBLE".equals(estado)) {
			throw conflicto("La habitación " + ficha.get("numero") + " está " + estado + ": debe estar DISPONIBLE para el check-in");
		}
		Map<String, Object> huesped = externos.huesped(r.getHuespedDocumento());
		int personas = 1 + (huesped.get("acompanantes") instanceof List<?> l ? l.size() : 0);
		int capacidad = (int) numero(ficha.get("capacidad"));
		if (personas > capacidad) {
			throw conflicto("La habitación " + ficha.get("numero") + " es para " + capacidad + " persona(s) y el huésped viene con "
					+ personas + " (contando acompañantes)");
		}
		externos.cambiarEstadoHabitacion(r.getHabitacionId(), "OCUPADA", texto(usuario));
		r.setEstado(EstadoReserva.EN_CURSO);
		r.setCheckIn(ahora());
		return reservas.save(r);
	}

	public Reserva checkOut(String id, String usuario) {
		Reserva r = reserva(id);
		if (r.getEstado() != EstadoReserva.EN_CURSO) {
			throw conflicto("Solo se puede hacer check-out de estadías en curso; la reserva " + r.getCodigo() + " está " + r.getEstado());
		}
		Map<String, Object> solicitud = new LinkedHashMap<>();
		solicitud.put("reservaId", r.getId());
		solicitud.put("huespedDocumento", r.getHuespedDocumento());
		solicitud.put("numeroHabitacion", r.getNumeroHabitacion());
		solicitud.put("noches", r.getNoches());
		solicitud.put("valorAlojamiento", r.getValorAlojamiento());
		Map<String, Object> factura = externos.generarFactura(solicitud);
		externos.cambiarEstadoHabitacion(r.getHabitacionId(), "LIMPIEZA", texto(usuario));
		r.setEstado(EstadoReserva.CERRADA);
		r.setCheckOut(ahora());
		r.setFacturaId(String.valueOf(factura.get("id")));
		Reserva guardada = reservas.save(r);
		try {
			long total = reservas.countByHuespedDocumentoAndEstado(r.getHuespedDocumento(), EstadoReserva.CERRADA);
			externos.actualizarEstadias(r.getHuespedDocumento(), total);
		} catch (ResponseStatusException ex) {
			// HUE-09 es informativo: si ms-huespedes no responde, el check-out igual queda hecho
		}
		return guardada;
	}

	// ======================= Consultas para otros microservicios =======================

	/** Llegadas confirmadas de una fecha (las usa ms-habitaciones para priorizar la limpieza). */
	public List<Reserva> llegadas(LocalDate fecha) {
		return reservas.findByEstadoAndLlegada(EstadoReserva.CONFIRMADA, fecha == null ? hoy() : fecha);
	}

	/** Reservas confirmadas o en curso de una habitación (HAB-04). */
	public List<Reserva> activas(String habitacionId) {
		return reservas.findByHabitacionIdAndEstadoIn(habitacionId, ACTIVAS);
	}

	// ======================= Reporte (RES-10) =======================

	public ReporteReservas reporte(LocalDate desde, LocalDate hasta, String tipoId) {
		if (desde == null || hasta == null || hasta.isBefore(desde)) {
			throw invalido("Indica un rango de fechas válido (desde y hasta)");
		}
		List<Reserva> lista = listar(null, null, null, desde, hasta).stream()
				.filter(r -> vacio(tipoId) || tipoId.equals(r.getTipoId())).toList();
		Map<String, Long> porEstado = new LinkedHashMap<>();
		for (EstadoReserva e : EstadoReserva.values()) {
			porEstado.put(e.name(), lista.stream().filter(r -> r.getEstado() == e).count());
		}
		ReporteReservas rep = new ReporteReservas();
		rep.setDesde(desde);
		rep.setHasta(hasta);
		rep.setTotal(lista.size());
		rep.setPorEstado(porEstado);
		rep.setValorAlojamiento(lista.stream().filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
				.mapToDouble(Reserva::getValorAlojamiento).sum());
		return rep;
	}

	// ======================= Ayudas =======================

	private void validarFechas(LocalDate llegada, LocalDate salida) {
		if (llegada == null || salida == null) {
			throw invalido("Escribe la fecha de llegada y la fecha de salida");
		}
		if (!llegada.isBefore(salida)) {
			throw invalido("La fecha de llegada debe ser anterior a la fecha de salida");
		}
	}

	private LocalDate hoy() {
		return LocalDate.now(zona);
	}

	private LocalDateTime ahora() {
		return LocalDateTime.now(zona).withNano(0);
	}

	private static boolean vacio(String s) {
		return s == null || s.isBlank();
	}

	private static String texto(String s) {
		return vacio(s) ? "recepción" : s;
	}

	private static double numero(Object o) {
		return o instanceof Number n ? n.doubleValue() : Double.parseDouble(String.valueOf(o));
	}

	private void obligatorio(String valor, String campo) {
		if (vacio(valor)) {
			throw invalido("Falta " + campo);
		}
	}

	private ResponseStatusException conflicto(String mensaje) {
		return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
	}

	private ResponseStatusException invalido(String mensaje) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
	}
}
