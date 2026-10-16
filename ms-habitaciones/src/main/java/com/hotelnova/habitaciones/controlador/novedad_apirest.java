package com.hotelnova.habitaciones.controlador;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelnova.habitaciones.modelo.Novedad;
import com.hotelnova.habitaciones.repositorio.reglas_repositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Novedades", description = "Historial de limpiezas y daños reportados por el personal de limpieza")
@RestController
@RequestMapping("/api/novedades")
public class novedad_apirest {

	private final reglas_repositorio reglas;

	public novedad_apirest(reglas_repositorio reglas) {
		this.reglas = reglas;
	}

	@Operation(summary = "Historial de limpiezas y daños (HAB-14), filtrable por habitación y fechas")
	@GetMapping
	public List<Novedad> historial(@RequestParam(name = "habitacionId", required = false) String habitacionId,
			@RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
		return reglas.historial(habitacionId, desde, hasta);
	}
}
