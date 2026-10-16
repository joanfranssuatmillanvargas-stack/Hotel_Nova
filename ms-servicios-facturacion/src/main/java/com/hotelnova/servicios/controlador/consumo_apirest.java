package com.hotelnova.servicios.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hotelnova.servicios.modelo.Consumo;
import com.hotelnova.servicios.modelo.CuentaEstadia;
import com.hotelnova.servicios.repositorio.reglas_repositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Consumos", description = "Consumos de servicios cargados a la estadía de un huésped")
@RestController
@RequestMapping("/api/consumos")
public class consumo_apirest {

	private final reglas_repositorio reglas;

	public consumo_apirest(reglas_repositorio reglas) {
		this.reglas = reglas;
	}

	@Operation(summary = "Consumos de una estadía")
	@GetMapping
	public List<Consumo> listar(@RequestParam("reservaId") String reservaId) {
		return reglas.cuenta(reservaId).getConsumos();
	}

	@Operation(summary = "Cuenta de la estadía: consumos y total acumulado (SER-05)")
	@GetMapping("/cuenta/{reservaId}")
	public CuentaEstadia cuenta(@PathVariable("reservaId") String reservaId) {
		return reglas.cuenta(reservaId);
	}

	@Operation(summary = "Registrar consumo (SER-04): solo a estadías con check-in (consulta ms-reservas)")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Consumo registrar(@RequestBody Consumo datos) {
		return reglas.registrarConsumo(datos);
	}

	@Operation(summary = "Anular consumo antes del check-out, indicando el motivo (SER-09)")
	@PostMapping("/{id}/anular")
	public Consumo anular(@PathVariable("id") String id, @RequestBody Map<String, String> datos) {
		return reglas.anularConsumo(id, datos.get("motivo"));
	}
}
