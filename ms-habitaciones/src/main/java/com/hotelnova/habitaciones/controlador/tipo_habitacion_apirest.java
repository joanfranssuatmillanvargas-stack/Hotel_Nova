package com.hotelnova.habitaciones.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hotelnova.habitaciones.modelo.TipoHabitacion;
import com.hotelnova.habitaciones.repositorio.reglas_repositorio;
import com.hotelnova.habitaciones.repositorio.tipo_habitacion_repositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tipos de habitación", description = "Sencilla, doble, suite... con capacidad y tarifa base")
@RestController
@RequestMapping("/api/tipos")
public class tipo_habitacion_apirest {

	private final tipo_habitacion_repositorio tipos;
	private final reglas_repositorio reglas;

	public tipo_habitacion_apirest(tipo_habitacion_repositorio tipos, reglas_repositorio reglas) {
		this.tipos = tipos;
		this.reglas = reglas;
	}

	@Operation(summary = "Listar tipos de habitación")
	@GetMapping
	public List<TipoHabitacion> listar() {
		return tipos.findAllByOrderByTarifaBaseAsc();
	}

	@Operation(summary = "Buscar un tipo por id")
	@GetMapping("/{id}")
	public TipoHabitacion buscar(@PathVariable("id") String id) {
		return reglas.tipo(id);
	}

	@Operation(summary = "Crear tipo (HAB-05): nombre único y tarifa base mayor que 0")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TipoHabitacion crear(@RequestBody TipoHabitacion datos) {
		return reglas.guardarTipo(null, datos);
	}

	@Operation(summary = "Editar tipo (HAB-05)")
	@PutMapping("/{id}")
	public TipoHabitacion actualizar(@PathVariable("id") String id, @RequestBody TipoHabitacion datos) {
		return reglas.guardarTipo(id, datos);
	}

	@Operation(summary = "Eliminar tipo (409 si alguna habitación lo usa)")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable("id") String id) {
		reglas.eliminarTipo(id);
	}
}
