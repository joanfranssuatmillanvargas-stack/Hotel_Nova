package com.hotelnova.huespedes.controlador;

import java.util.List;
import java.util.Map;

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

import com.hotelnova.huespedes.modelo.Acompanante;
import com.hotelnova.huespedes.modelo.Huesped;
import com.hotelnova.huespedes.modelo.ResumenEstadias;
import com.hotelnova.huespedes.repositorio.reglas_repositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** API REST de huéspedes. El huésped se identifica por su número de documento. */
@Tag(name = "Huéspedes", description = "Datos personales, acompañantes e historial de estadías")
@RestController
@RequestMapping("/api/huespedes")
public class huesped_apirest {

	private final reglas_repositorio reglas;

	public huesped_apirest(reglas_repositorio reglas) {
		this.reglas = reglas;
	}

	@Operation(summary = "Listar o buscar huéspedes por documento o nombre (HUE-03)")
	@GetMapping
	public List<Huesped> listar(@RequestParam(name = "q", required = false) String q) {
		return reglas.listar(q);
	}

	@Operation(summary = "Buscar huésped por documento (HUE-10)")
	@GetMapping("/{documento}")
	public Huesped buscar(@PathVariable("documento") String documento) {
		return reglas.buscar(documento);
	}

	@Operation(summary = "Registrar huésped (HUE-01): documento único y correo válido")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Huesped crear(@RequestBody Huesped datos) {
		return reglas.crear(datos);
	}

	@Operation(summary = "Editar datos del huésped (HUE-02, HUE-07)")
	@PutMapping("/{documento}")
	public Huesped actualizar(@PathVariable("documento") String documento, @RequestBody Huesped datos) {
		return reglas.actualizar(documento, datos);
	}

	@Operation(summary = "Eliminar huésped (HUE-04): 409 si tiene reservas")
	@DeleteMapping("/{documento}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable("documento") String documento) {
		reglas.eliminar(documento);
	}

	@Operation(summary = "Agregar acompañante (HUE-05)")
	@PostMapping("/{documento}/acompanantes")
	public Huesped agregarAcompanante(@PathVariable("documento") String documento, @RequestBody Acompanante acompanante) {
		return reglas.agregarAcompanante(documento, acompanante);
	}

	@Operation(summary = "Quitar acompañante (HUE-05)")
	@DeleteMapping("/{documento}/acompanantes/{documentoAcompanante}")
	public Huesped quitarAcompanante(@PathVariable("documento") String documento,
			@PathVariable("documentoAcompanante") String documentoAcompanante) {
		return reglas.quitarAcompanante(documento, documentoAcompanante);
	}

	@Operation(summary = "Historial de estadías consultando ms-reservas (HUE-08)")
	@GetMapping("/{documento}/historial")
	public List<Map<String, Object>> historial(@PathVariable("documento") String documento) {
		return reglas.historial(documento);
	}

	@Operation(summary = "Actualizar número de estadías y marca de frecuente (HUE-09, lo llama ms-reservas)")
	@PostMapping("/{documento}/estadias")
	public Huesped estadias(@PathVariable("documento") String documento, @RequestBody ResumenEstadias resumen) {
		return reglas.actualizarEstadias(documento, resumen.getTotal());
	}
}
