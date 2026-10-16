package com.hotelnova.habitaciones.controlador;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hotelnova.habitaciones.modelo.CambioEstado;
import com.hotelnova.habitaciones.modelo.EstadoHabitacion;
import com.hotelnova.habitaciones.modelo.FichaHabitacion;
import com.hotelnova.habitaciones.modelo.Habitacion;
import com.hotelnova.habitaciones.modelo.Ocupacion;
import com.hotelnova.habitaciones.modelo.PendienteLimpieza;
import com.hotelnova.habitaciones.modelo.ReporteLimpieza;
import com.hotelnova.habitaciones.modelo.ValorEstadia;
import com.hotelnova.habitaciones.repositorio.reglas_repositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Habitaciones", description = "Habitaciones, estado, tarifas, ocupación y flujo de limpieza")
@RestController
@RequestMapping("/api/habitaciones")
public class habitacion_apirest {

	private final reglas_repositorio reglas;

	public habitacion_apirest(reglas_repositorio reglas) {
		this.reglas = reglas;
	}

	@Operation(summary = "Listar habitaciones (HAB-03) con filtros opcionales por estado, tipo y piso")
	@GetMapping
	public List<Habitacion> listar(@RequestParam(name = "estado", required = false) EstadoHabitacion estado,
			@RequestParam(name = "tipoId", required = false) String tipoId,
			@RequestParam(name = "piso", required = false) Integer piso) {
		return reglas.listar(estado, tipoId, piso);
	}

	@Operation(summary = "Buscar habitación por id (HAB-10)")
	@GetMapping("/{id}")
	public Habitacion buscar(@PathVariable("id") String id) {
		return reglas.habitacion(id);
	}

	@Operation(summary = "Habitación con los datos de su tipo: capacidad y tarifa (HAB-10)")
	@GetMapping("/{id}/ficha")
	public FichaHabitacion ficha(@PathVariable("id") String id) {
		return reglas.ficha(id);
	}

	@Operation(summary = "Valor del alojamiento noche por noche, con tarifas de temporada (HAB-06, RES-09)")
	@GetMapping("/{id}/valor")
	public ValorEstadia valor(@PathVariable("id") String id,
			@RequestParam("llegada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate llegada,
			@RequestParam("salida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate salida) {
		return reglas.valor(id, llegada, salida);
	}

	@Operation(summary = "Habitaciones que se pueden ofrecer: excluye las de mantenimiento (HAB-08)")
	@GetMapping("/ofrecibles")
	public List<FichaHabitacion> ofrecibles(@RequestParam(name = "tipoId", required = false) String tipoId) {
		return reglas.ofrecibles(tipoId);
	}

	@Operation(summary = "Tablero de ocupación: habitaciones por estado y porcentaje (HAB-09)")
	@GetMapping("/ocupacion")
	public Ocupacion ocupacion() {
		return reglas.ocupacion();
	}

	@Operation(summary = "Pendientes de limpieza, primero las que tienen llegada hoy (HAB-11)")
	@GetMapping("/limpieza")
	public List<PendienteLimpieza> pendientesLimpieza() {
		return reglas.pendientesLimpieza();
	}

	@Operation(summary = "Registrar habitación (HAB-01): número único, queda DISPONIBLE")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Habitacion crear(@RequestBody Habitacion datos) {
		return reglas.guardarHabitacion(null, datos);
	}

	@Operation(summary = "Editar habitación (HAB-02)")
	@PutMapping("/{id}")
	public Habitacion actualizar(@PathVariable("id") String id, @RequestBody Habitacion datos) {
		return reglas.guardarHabitacion(id, datos);
	}

	@Operation(summary = "Cambiar estado (HAB-07): DISPONIBLE, OCUPADA, MANTENIMIENTO o LIMPIEZA")
	@PatchMapping("/{id}/estado")
	public Habitacion cambiarEstado(@PathVariable("id") String id, @RequestBody CambioEstado cambio) {
		return reglas.cambiarEstado(id, cambio.getEstado(), cambio.getUsuario());
	}

	@Operation(summary = "Marcar habitación como limpia (HAB-12): solo desde LIMPIEZA")
	@PostMapping("/{id}/limpia")
	public Habitacion marcarLimpia(@PathVariable("id") String id, @RequestBody ReporteLimpieza datos) {
		return reglas.marcarLimpia(id, datos.getEmpleado());
	}

	@Operation(summary = "Reportar daño o novedad (HAB-13): la habitación pasa a MANTENIMIENTO")
	@PostMapping("/{id}/danos")
	public Habitacion reportarDano(@PathVariable("id") String id, @RequestBody ReporteLimpieza datos) {
		return reglas.reportarDano(id, datos.getEmpleado(), datos.getDescripcion());
	}

	@Operation(summary = "Eliminar habitación (HAB-04): 409 si tiene reservas activas")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable("id") String id) {
		reglas.eliminarHabitacion(id);
	}
}
