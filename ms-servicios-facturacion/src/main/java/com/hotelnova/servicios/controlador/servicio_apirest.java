package com.hotelnova.servicios.controlador;

import java.util.List;

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

import com.hotelnova.servicios.modelo.Categoria;
import com.hotelnova.servicios.modelo.Servicio;
import com.hotelnova.servicios.repositorio.reglas_repositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Servicios", description = "Catálogo de servicios adicionales: restaurante, lavandería, parqueadero y spa")
@RestController
@RequestMapping("/api/servicios")
public class servicio_apirest {

	private final reglas_repositorio reglas;

	public servicio_apirest(reglas_repositorio reglas) {
		this.reglas = reglas;
	}

	@Operation(summary = "Catálogo por categoría (SER-03); soloActivos=true oculta los desactivados")
	@GetMapping
	public List<Servicio> listar(@RequestParam(name = "categoria", required = false) Categoria categoria,
			@RequestParam(name = "soloActivos", defaultValue = "false") boolean soloActivos) {
		return reglas.catalogo(categoria, soloActivos);
	}

	@Operation(summary = "Buscar servicio por id")
	@GetMapping("/{id}")
	public Servicio buscar(@PathVariable("id") String id) {
		return reglas.servicio(id);
	}

	@Operation(summary = "Registrar servicio (SER-01): precio mayor que 0 y categoría válida")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Servicio crear(@RequestBody Servicio datos) {
		return reglas.guardarServicio(null, datos);
	}

	@Operation(summary = "Editar servicio (SER-02)")
	@PutMapping("/{id}")
	public Servicio actualizar(@PathVariable("id") String id, @RequestBody Servicio datos) {
		return reglas.guardarServicio(id, datos);
	}

	@Operation(summary = "Activar o desactivar servicio (SER-02)")
	@PatchMapping("/{id}/activo")
	public Servicio activo(@PathVariable("id") String id, @RequestParam("valor") boolean valor) {
		return reglas.cambiarActivo(id, valor);
	}

	@Operation(summary = "Eliminar servicio (409 si ya tiene consumos: se debe desactivar)")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable("id") String id) {
		reglas.eliminarServicio(id);
	}
}
