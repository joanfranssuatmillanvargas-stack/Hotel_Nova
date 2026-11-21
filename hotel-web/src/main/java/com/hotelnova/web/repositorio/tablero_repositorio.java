package com.hotelnova.web.repositorio;

import static com.hotelnova.web.repositorio.microservicios_repositorio.HABITACIONES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.HUESPEDES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.RESERVAS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.SERVICIOS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.consulta;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

/**
 * Tablero gerencial (PLA-10; R2-Sprint 3, T024 · REQ-HN-018, 019 · ADR-009). Solo lee: junta los reportes que
 * ya tienen los 4 microservicios por REST y no guarda nada.
 */
@Repository
public class tablero_repositorio {

	private final microservicios_repositorio ms;

	public tablero_repositorio(microservicios_repositorio ms) {
		this.ms = ms;
	}

	public Map<String, Object> tablero(LocalDate desde, LocalDate hasta) {
		if (desde == null || hasta == null || hasta.isBefore(desde)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha \"hasta\" no puede ser anterior a \"desde\"");
		}
		String periodo = consulta("desde", desde, "hasta", hasta);
		Map<String, Object> ocupacion = ms.objeto(HABITACIONES, "/api/habitaciones/ocupacion");
		Map<String, Object> reservas = ms.objeto(RESERVAS, "/api/reservas/reporte" + periodo);
		Map<String, Object> ingresos = ms.objeto(SERVICIOS, "/api/facturas/reporte" + periodo);

		Map<String, Object> t = new LinkedHashMap<>();
		t.put("desde", desde.toString());
		t.put("hasta", hasta.toString());
		// ocupación del día (ms-habitaciones): habitaciones OCUPADAS / total
		t.put("ocupacion", numero(ocupacion.get("porcentajeOcupacion")));
		t.put("habitaciones", (long) numero(ocupacion.get("total")));
		Object porEstado = ocupacion.get("porEstado");
		t.put("ocupadas", porEstado instanceof Map<?, ?> m ? (long) numero(m.get("OCUPADA")) : 0L);
		t.put("porEstado", porEstado == null ? Map.of() : porEstado);
		// tarifa promedio por noche vendida (ms-reservas)
		t.put("tarifaPromedio", numero(reservas.get("tarifaPromedio")));
		t.put("noches", (long) numero(reservas.get("noches")));
		t.put("reservas", (long) numero(reservas.get("total")));
		// ingresos del periodo por categoría (ms-servicios-facturacion): alojamiento y cada categoría de servicio
		Map<String, Double> categorias = new LinkedHashMap<>();
		categorias.put("Alojamiento", numero(ingresos.get("alojamiento")));
		if (ingresos.get("porCategoria") instanceof Map<?, ?> m) {
			m.forEach((k, v) -> categorias.put(String.valueOf(k), numero(v)));
		}
		t.put("ingresosPorCategoria", categorias);
		t.put("iva", numero(ingresos.get("iva")));
		t.put("ingresosTotal", numero(ingresos.get("total")));
		t.put("pagado", numero(ingresos.get("pagado")));
		t.put("facturas", (long) numero(ingresos.get("facturas")));
		// huéspedes registrados (ms-huespedes) y temporadas que esperan la decisión del gerente (HAB-20)
		t.put("huespedes", ms.lista(HUESPEDES, "/api/huespedes").size());
		t.put("temporadasPendientes", ms.lista(HABITACIONES, "/api/temporadas?estado=PENDIENTE").size());
		return t;
	}

	private static double numero(Object o) {
		return o instanceof Number n ? n.doubleValue() : 0;
	}
}
