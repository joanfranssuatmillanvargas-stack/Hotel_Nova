package com.hotelnova.reservas.controlador;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hotelnova.reservas.modelo.Disponibilidad;
import com.hotelnova.reservas.modelo.EstadoReserva;
import com.hotelnova.reservas.modelo.ReporteReservas;
import com.hotelnova.reservas.modelo.Reserva;
import com.hotelnova.reservas.repositorio.reglas_repositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** API REST de reservas (documentada en /swagger-ui.html). Las reservas no se borran: se cancelan. */
@Tag(name = "Reservas", description = "Reservas, disponibilidad, check-in, check-out y reportes")
@RestController
@RequestMapping("/api/reservas")
public class reserva_apirest {

	private final reglas_repositorio reglas;

	public reserva_apirest(reglas_repositorio reglas) {
		this.reglas = reglas;
	}

	@Operation(summary = "Consultar reservas (RES-02) por huésped, estado, habitación o rango de llegada")
	@GetMapping
	public List<Reserva> listar(@RequestParam(name = "huespedDocumento", required = false) String huespedDocumento,
			@RequestParam(name = "estado", required = false) EstadoReserva estado,
			@RequestParam(name = "habitacionId", required = false) String habitacionId,
			@RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
		return reglas.listar(huespedDocumento, estado, habitacionId, desde, hasta);
	}

	@Operation(summary = "Buscar reserva por id")
	@GetMapping("/{id}")
	public Reserva buscar(@PathVariable("id") String id) {
		return reglas.reserva(id);
	}

	@Operation(summary = "Disponibilidad por fechas y tipo de habitación (RES-06)")
	@GetMapping("/disponibilidad")
	public List<Disponibilidad> disponibilidad(
			@RequestParam("llegada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate llegada,
			@RequestParam("salida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate salida,
			@RequestParam(name = "tipoId", required = false) String tipoId) {
		return reglas.disponibilidad(llegada, salida, tipoId);
	}

	@Operation(summary = "Crear reserva (RES-01, RES-05, RES-09): valida huésped, habitación, cruces y calcula el valor")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Reserva crear(@RequestBody Reserva datos) {
		return reglas.crear(datos);
	}

	@Operation(summary = "Modificar fechas o habitación (RES-03); vuelve a validar cruces")
	@PutMapping("/{id}")
	public Reserva modificar(@PathVariable("id") String id, @RequestBody Reserva datos) {
		return reglas.modificar(id, datos);
	}

	@Operation(summary = "Cancelar reserva (RES-04)")
	@PostMapping("/{id}/cancelar")
	public Reserva cancelar(@PathVariable("id") String id) {
		return reglas.cancelar(id);
	}

	@Operation(summary = "Check-in (RES-07): la habitación pasa a OCUPADA en ms-habitaciones")
	@PostMapping("/{id}/check-in")
	public Reserva checkIn(@PathVariable("id") String id, @RequestParam(name = "usuario", required = false) String usuario) {
		return reglas.checkIn(id, usuario);
	}

	@Operation(summary = "Check-out (RES-08): genera la factura en ms-servicios-facturacion y la habitación pasa a LIMPIEZA")
	@PostMapping("/{id}/check-out")
	public Reserva checkOut(@PathVariable("id") String id, @RequestParam(name = "usuario", required = false) String usuario) {
		return reglas.checkOut(id, usuario);
	}

	@Operation(summary = "Llegadas confirmadas de una fecha (las usa ms-habitaciones)")
	@GetMapping("/llegadas")
	public List<Reserva> llegadas(
			@RequestParam(name = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
		return reglas.llegadas(fecha);
	}

	@Operation(summary = "Reservas confirmadas o en curso de una habitación (las usa ms-habitaciones)")
	@GetMapping("/activas")
	public List<Reserva> activas(@RequestParam("habitacionId") String habitacionId) {
		return reglas.activas(habitacionId);
	}

	@Operation(summary = "Reporte de reservas por rango de fechas y tipo (RES-10)")
	@GetMapping("/reporte")
	public ReporteReservas reporte(
			@RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
			@RequestParam(name = "tipoId", required = false) String tipoId) {
		return reglas.reporte(desde, hasta, tipoId);
	}
}
