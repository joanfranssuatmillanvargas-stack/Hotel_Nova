package com.hotelnova.habitaciones.repositorio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.habitaciones.modelo.EstadoHabitacion;
import com.hotelnova.habitaciones.modelo.FichaHabitacion;
import com.hotelnova.habitaciones.modelo.Habitacion;
import com.hotelnova.habitaciones.modelo.Novedad;
import com.hotelnova.habitaciones.modelo.Ocupacion;
import com.hotelnova.habitaciones.modelo.PendienteLimpieza;
import com.hotelnova.habitaciones.modelo.Temporada;
import com.hotelnova.habitaciones.modelo.TipoHabitacion;
import com.hotelnova.habitaciones.modelo.TipoNovedad;
import com.hotelnova.habitaciones.modelo.ValorEstadia;

/**
 * Reglas de negocio de ms-habitaciones. Usa los repositorios de MongoDB y, cuando
 * necesita datos de reservas, los pide por REST (externos_repositorio).
 * Los errores se lanzan como ResponseStatusException con su código HTTP.
 */
@Repository
public class reglas_repositorio {

	private final tipo_habitacion_repositorio tipos;
	private final habitacion_repositorio habitaciones;
	private final temporada_repositorio temporadas;
	private final novedad_repositorio novedades;
	private final externos_repositorio externos;
	private final ZoneId zona;

	public reglas_repositorio(tipo_habitacion_repositorio tipos, habitacion_repositorio habitaciones,
			temporada_repositorio temporadas, novedad_repositorio novedades, externos_repositorio externos,
			@Value("${hotelnova.zona}") String zona) {
		this.tipos = tipos;
		this.habitaciones = habitaciones;
		this.temporadas = temporadas;
		this.novedades = novedades;
		this.externos = externos;
		this.zona = ZoneId.of(zona);
	}

	// ======================= Tipos de habitación (HAB-05) =======================

	public TipoHabitacion tipo(String id) {
		return tipos.findById(id).orElseThrow(() -> noEncontrado("No existe un tipo de habitación con id " + id));
	}

	public TipoHabitacion guardarTipo(String id, TipoHabitacion datos) {
		obligatorio(datos.getNombre(), "el nombre del tipo de habitación");
		if (datos.getTarifaBase() <= 0) {
			throw invalido("La tarifa base debe ser mayor que 0");
		}
		if (datos.getCapacidad() < 1) {
			throw invalido("La capacidad debe ser de al menos 1 persona");
		}
		tipos.findByNombreIgnoreCase(datos.getNombre().trim()).ifPresent(otro -> {
			if (!otro.getId().equals(id)) {
				throw conflicto("Ya existe un tipo de habitación llamado " + otro.getNombre());
			}
		});
		if (id != null) {
			tipo(id);
		}
		datos.setId(id);
		datos.setNombre(datos.getNombre().trim());
		return tipos.save(datos);
	}

	public void eliminarTipo(String id) {
		TipoHabitacion tipo = tipo(id);
		long usadas = habitaciones.countByTipoId(id);
		if (usadas > 0) {
			throw conflicto("No se puede eliminar el tipo " + tipo.getNombre() + ": lo usan " + usadas + " habitación(es)");
		}
		temporadas.deleteAll(temporadas.findByTipoId(id));
		tipos.delete(tipo);
	}

	// ======================= Habitaciones (HAB-01 a HAB-04) =======================

	public Habitacion habitacion(String id) {
		return habitaciones.findById(id).orElseThrow(() -> noEncontrado("No existe una habitación con id " + id));
	}

	/** HAB-03: listado con filtros combinables. */
	public List<Habitacion> listar(EstadoHabitacion estado, String tipoId, Integer piso) {
		return habitaciones.findAllByOrderByNumeroAsc().stream()
				.filter(h -> estado == null || h.getEstado() == estado)
				.filter(h -> tipoId == null || tipoId.isBlank() || tipoId.equals(h.getTipoId()))
				.filter(h -> piso == null || h.getPiso() == piso)
				.toList();
	}

	public Habitacion guardarHabitacion(String id, Habitacion datos) {
		obligatorio(datos.getNumero(), "el número de la habitación");
		obligatorio(datos.getTipoId(), "el tipo de habitación");
		tipo(datos.getTipoId());
		String numero = datos.getNumero().trim();
		habitaciones.findByNumero(numero).ifPresent(otra -> {
			if (!otra.getId().equals(id)) {
				throw conflicto("Ya existe la habitación número " + numero);
			}
		});
		if (id == null) {
			datos.setEstado(EstadoHabitacion.DISPONIBLE);
			datos.setActualizadoEn(ahora());
		} else {
			Habitacion actual = habitacion(id);
			datos.setEstado(actual.getEstado());
			datos.setActualizadoPor(actual.getActualizadoPor());
			datos.setActualizadoEn(actual.getActualizadoEn());
		}
		datos.setId(id);
		datos.setNumero(numero);
		return habitaciones.save(datos);
	}

	/** HAB-04: no se elimina si tiene reservas confirmadas o en curso. */
	public void eliminarHabitacion(String id) {
		Habitacion habitacion = habitacion(id);
		List<Map<String, Object>> activas = externos.reservasActivas(id);
		if (!activas.isEmpty()) {
			throw conflicto("No se puede eliminar la habitación " + habitacion.getNumero() + ": tiene "
					+ activas.size() + " reserva(s) activa(s), por ejemplo " + activas.get(0).get("codigo"));
		}
		habitaciones.delete(habitacion);
	}

	/** HAB-07: cambia el estado y guarda quién lo hizo. */
	public Habitacion cambiarEstado(String id, EstadoHabitacion estado, String usuario) {
		if (estado == null) {
			throw invalido("Falta el nuevo estado");
		}
		Habitacion habitacion = habitacion(id);
		habitacion.setEstado(estado);
		habitacion.setActualizadoPor(usuario == null || usuario.isBlank() ? "sistema" : usuario);
		habitacion.setActualizadoEn(ahora());
		return habitaciones.save(habitacion);
	}

	/** HAB-10: habitación con los datos de su tipo (la usan ms-reservas y ms-servicios-facturacion). */
	public FichaHabitacion ficha(String id) {
		Habitacion h = habitacion(id);
		TipoHabitacion t = tipo(h.getTipoId());
		FichaHabitacion f = new FichaHabitacion();
		f.setId(h.getId());
		f.setNumero(h.getNumero());
		f.setPiso(h.getPiso());
		f.setTipoId(t.getId());
		f.setTipoNombre(t.getNombre());
		f.setCapacidad(t.getCapacidad());
		f.setTarifaBase(t.getTarifaBase());
		f.setEstado(h.getEstado());
		return f;
	}

	/** HAB-08: habitaciones que se pueden ofrecer (todas menos las que están en mantenimiento). */
	public List<FichaHabitacion> ofrecibles(String tipoId) {
		return habitaciones.findAllByOrderByNumeroAsc().stream()
				.filter(h -> h.getEstado() != EstadoHabitacion.MANTENIMIENTO)
				.filter(h -> tipoId == null || tipoId.isBlank() || tipoId.equals(h.getTipoId()))
				.map(h -> ficha(h.getId()))
				.toList();
	}

	// ======================= Tarifas (HAB-06) =======================

	public Temporada temporada(String id) {
		return temporadas.findById(id).orElseThrow(() -> noEncontrado("No existe una temporada con id " + id));
	}

	public Temporada guardarTemporada(String id, Temporada datos) {
		obligatorio(datos.getNombre(), "el nombre de la temporada");
		obligatorio(datos.getTipoId(), "el tipo de habitación");
		TipoHabitacion tipo = tipo(datos.getTipoId());
		if (datos.getInicio() == null || datos.getFin() == null) {
			throw invalido("Escribe la fecha de inicio y la fecha de fin de la temporada");
		}
		if (datos.getFin().isBefore(datos.getInicio())) {
			throw invalido("La fecha de fin no puede ser anterior a la de inicio");
		}
		if (datos.getTarifa() <= 0) {
			throw invalido("La tarifa de la temporada debe ser mayor que 0");
		}
		for (Temporada otra : temporadas.findByTipoId(datos.getTipoId())) {
			boolean seCruzan = !datos.getInicio().isAfter(otra.getFin()) && !datos.getFin().isBefore(otra.getInicio());
			if (seCruzan && !otra.getId().equals(id)) {
				throw conflicto("La temporada se cruza con \"" + otra.getNombre() + "\" (" + otra.getInicio() + " a "
						+ otra.getFin() + ") del tipo " + tipo.getNombre());
			}
		}
		if (id != null) {
			temporada(id);
		}
		datos.setId(id);
		return temporadas.save(datos);
	}

	/** Tarifa de una noche: la de la temporada que contiene la fecha, o la tarifa base. */
	public double tarifaNoche(TipoHabitacion tipo, List<Temporada> temporadasTipo, LocalDate noche) {
		for (Temporada t : temporadasTipo) {
			if (!noche.isBefore(t.getInicio()) && !noche.isAfter(t.getFin())) {
				return t.getTarifa();
			}
		}
		return tipo.getTarifaBase();
	}

	/** RES-09 / HAB-06: valor del alojamiento sumando la tarifa de cada noche. */
	public ValorEstadia valor(String habitacionId, LocalDate llegada, LocalDate salida) {
		if (llegada == null || salida == null || !llegada.isBefore(salida)) {
			throw invalido("La fecha de llegada debe ser anterior a la fecha de salida");
		}
		Habitacion h = habitacion(habitacionId);
		TipoHabitacion tipo = tipo(h.getTipoId());
		List<Temporada> temporadasTipo = temporadas.findByTipoId(tipo.getId());
		double total = 0;
		for (LocalDate noche = llegada; noche.isBefore(salida); noche = noche.plusDays(1)) {
			total += tarifaNoche(tipo, temporadasTipo, noche);
		}
		ValorEstadia v = new ValorEstadia();
		v.setHabitacionId(habitacionId);
		v.setLlegada(llegada);
		v.setSalida(salida);
		v.setNoches(ChronoUnit.DAYS.between(llegada, salida));
		v.setValor(total);
		return v;
	}

	// ======================= Ocupación (HAB-09) =======================

	public Ocupacion ocupacion() {
		Ocupacion o = new Ocupacion();
		Map<String, Long> porEstado = new LinkedHashMap<>();
		long total = 0;
		for (EstadoHabitacion e : EstadoHabitacion.values()) {
			long n = habitaciones.countByEstado(e);
			porEstado.put(e.name(), n);
			total += n;
		}
		o.setTotal(total);
		o.setPorEstado(porEstado);
		long ocupadas = porEstado.get(EstadoHabitacion.OCUPADA.name());
		o.setPorcentajeOcupacion(total == 0 ? 0 : Math.round(ocupadas * 1000.0 / total) / 10.0);
		return o;
	}

	// ======================= Limpieza (HAB-11 a HAB-14) =======================

	/** HAB-11: pendientes de limpieza; primero las que tienen una llegada hoy. */
	public List<PendienteLimpieza> pendientesLimpieza() {
		Set<String> conLlegada = new HashSet<>();
		try {
			for (Map<String, Object> r : externos.llegadas(hoy())) {
				conLlegada.add(String.valueOf(r.get("habitacionId")));
			}
		} catch (ResponseStatusException ex) {
			// si ms-reservas no responde, igual se muestra la lista (sin prioridad)
		}
		List<PendienteLimpieza> lista = new ArrayList<>();
		for (Habitacion h : habitaciones.findByEstadoOrderByNumeroAsc(EstadoHabitacion.LIMPIEZA)) {
			PendienteLimpieza p = new PendienteLimpieza();
			p.setHabitacionId(h.getId());
			p.setNumero(h.getNumero());
			p.setPiso(h.getPiso());
			p.setLlegadaHoy(conLlegada.contains(h.getId()));
			lista.add(p);
		}
		lista.sort(Comparator.comparing(PendienteLimpieza::isLlegadaHoy).reversed()
				.thenComparing(PendienteLimpieza::getNumero));
		return lista;
	}

	/** HAB-12: solo desde LIMPIEZA; queda DISPONIBLE y se registra quién la limpió. */
	public Habitacion marcarLimpia(String id, String empleado) {
		obligatorio(empleado, "el nombre del empleado");
		Habitacion h = habitacion(id);
		if (h.getEstado() != EstadoHabitacion.LIMPIEZA) {
			throw conflicto("La habitación " + h.getNumero() + " está " + h.getEstado()
					+ ": solo se pueden marcar como limpias las habitaciones en estado LIMPIEZA");
		}
		registrar(h, TipoNovedad.LIMPIEZA, "Habitación limpia", empleado, false);
		// si se reportó un daño mientras estaba ocupada, ahora pasa a mantenimiento
		boolean danoPendiente = false;
		for (Novedad n : novedades.findByHabitacionIdOrderByFechaDesc(id)) {
			if (n.isPendiente()) {
				n.setPendiente(false);
				novedades.save(n);
				danoPendiente = true;
			}
		}
		return cambiarEstado(id, danoPendiente ? EstadoHabitacion.MANTENIMIENTO : EstadoHabitacion.DISPONIBLE, empleado);
	}

	/** HAB-13: reporta un daño; la habitación pasa a MANTENIMIENTO (si no está ocupada). */
	public Habitacion reportarDano(String id, String empleado, String descripcion) {
		obligatorio(empleado, "el nombre del empleado");
		obligatorio(descripcion, "la descripción del daño");
		Habitacion h = habitacion(id);
		boolean ocupada = h.getEstado() == EstadoHabitacion.OCUPADA || h.getEstado() == EstadoHabitacion.LIMPIEZA;
		registrar(h, TipoNovedad.DANO, descripcion.trim(), empleado, ocupada);
		if (ocupada) {
			return h; // queda pendiente: pasa a mantenimiento cuando se limpie después de la salida
		}
		return cambiarEstado(id, EstadoHabitacion.MANTENIMIENTO, empleado);
	}

	/** HAB-14: historial filtrable por habitación y fechas. */
	public List<Novedad> historial(String habitacionId, LocalDate desde, LocalDate hasta) {
		List<Novedad> lista = habitacionId == null || habitacionId.isBlank() ? novedades.findAllByOrderByFechaDesc()
				: novedades.findByHabitacionIdOrderByFechaDesc(habitacionId);
		return lista.stream()
				.filter(n -> desde == null || !n.getFecha().toLocalDate().isBefore(desde))
				.filter(n -> hasta == null || !n.getFecha().toLocalDate().isAfter(hasta))
				.toList();
	}

	private void registrar(Habitacion h, TipoNovedad tipo, String descripcion, String empleado, boolean pendiente) {
		Novedad n = new Novedad();
		n.setHabitacionId(h.getId());
		n.setNumeroHabitacion(h.getNumero());
		n.setTipo(tipo);
		n.setDescripcion(descripcion);
		n.setEmpleado(empleado.trim());
		n.setFecha(ahora());
		n.setPendiente(pendiente);
		novedades.save(n);
	}

	// ======================= Ayudas =======================

	private LocalDate hoy() {
		return LocalDate.now(zona);
	}

	private LocalDateTime ahora() {
		return LocalDateTime.now(zona).withNano(0);
	}

	private void obligatorio(String valor, String campo) {
		if (valor == null || valor.isBlank()) {
			throw invalido("Falta " + campo);
		}
	}

	private ResponseStatusException noEncontrado(String mensaje) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, mensaje); // 404
	}

	private ResponseStatusException conflicto(String mensaje) {
		return new ResponseStatusException(HttpStatus.CONFLICT, mensaje); // 409
	}

	private ResponseStatusException invalido(String mensaje) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje); // 400
	}
}
