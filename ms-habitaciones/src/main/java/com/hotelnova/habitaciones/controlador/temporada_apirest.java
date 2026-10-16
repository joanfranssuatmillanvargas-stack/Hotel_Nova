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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hotelnova.habitaciones.modelo.Temporada;
import com.hotelnova.habitaciones.repositorio.reglas_repositorio;
import com.hotelnova.habitaciones.repositorio.temporada_repositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Temporadas", description = "Tarifas por temporada (alta, baja) de cada tipo de habitación")
@RestController
@RequestMapping("/api/temporadas")
public class temporada_apirest {

	private final temporada_repositorio temporadas;
	private final reglas_repositorio reglas;

	public temporada_apirest(temporada_repositorio temporadas, reglas_repositorio reglas) {
		this.temporadas = temporadas;
		this.reglas = reglas;
	}

	@Operation(summary = "Listar temporadas (opcional: por tipo)")
	@GetMapping
	public List<Temporada> listar(@RequestParam(name = "tipoId", required = false) String tipoId) {
		return tipoId == null || tipoId.isBlank() ? temporadas.findAllByOrderByInicioAsc() : temporadas.findByTipoId(tipoId);
	}

	@Operation(summary = "Buscar temporada por id")
	@GetMapping("/{id}")
	public Temporada buscar(@PathVariable("id") String id) {
		return reglas.temporada(id);
	}

	@Operation(summary = "Crear temporada (HAB-06): no se puede cruzar con otra del mismo tipo")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Temporada crear(@RequestBody Temporada datos) {
		return reglas.guardarTemporada(null, datos);
	}

	@Operation(summary = "Editar temporada (HAB-06)")
	@PutMapping("/{id}")
	public Temporada actualizar(@PathVariable("id") String id, @RequestBody Temporada datos) {
		return reglas.guardarTemporada(id, datos);
	}

	@Operation(summary = "Eliminar temporada")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable("id") String id) {
		temporadas.delete(reglas.temporada(id));
	}
}
