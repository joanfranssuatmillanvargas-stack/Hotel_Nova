package com.hotelnova.servicios.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.hotelnova.servicios.modelo.CierreCaja;
import com.hotelnova.servicios.modelo.EstadoFactura;
import com.hotelnova.servicios.modelo.Factura;
import com.hotelnova.servicios.modelo.MedioPago;
import com.hotelnova.servicios.modelo.Pago;

/**
 * T012 (R2-Sprint 2, PBI-04): pruebas del pago por el cajero y del cierre de caja, escritas antes del código.
 * SER-11: AC-009.1 y AC-010.1 (REQ-HN-009, 010). SER-12: AC-012.1 y AC-012.2 (REQ-HN-012).
 */
class CajaTest {

	private factura_repositorio facturas;
	private reglas_repositorio reglas;

	@BeforeEach
	void preparar() {
		facturas = mock(factura_repositorio.class);
		reglas = new reglas_repositorio(mock(servicio_repositorio.class), mock(consumo_repositorio.class), facturas,
				mock(contador_repositorio.class), mock(externos_repositorio.class), "America/Bogota", 0.19);
		when(facturas.save(any(Factura.class))).thenAnswer(i -> i.getArgument(0));
	}

	// ---------------------------------------------------------------- SER-11

	@Test
	void ac009_1_elCajeroRegistraElPagoYQuedaComoQuienCobro() {
		Factura f12 = pendiente("f12", 12, 350000);
		when(facturas.findById("f12")).thenReturn(Optional.of(f12));

		Factura pagada = reglas.pagar("f12", MedioPago.EFECTIVO, "cajero");

		assertEquals(EstadoFactura.PAGADA, pagada.getEstado());
		assertEquals(MedioPago.EFECTIVO, pagada.getPago().getMedio());
		assertEquals("cajero", pagada.getPago().getCajero());
		assertEquals(350000, pagada.getPago().getValor());
		assertTrue(pagada.getPago().getFecha() != null);
	}

	@Test
	void ac010_1_unaFacturaPagadaNoSeCobraDosVeces() {
		Factura f12 = pendiente("f12", 12, 350000);
		f12.setEstado(EstadoFactura.PAGADA);
		when(facturas.findById("f12")).thenReturn(Optional.of(f12));

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.pagar("f12", MedioPago.EFECTIVO, "cajero"));

		assertEquals(409, ex.getStatusCode().value());
		assertTrue(ex.getReason().contains("ya está pagada"), ex.getReason());
		verify(facturas, never()).save(any());
	}

	@Test
	void req009_sinCajeroNoSeRegistraElPago() {
		when(facturas.findById("f12")).thenReturn(Optional.of(pendiente("f12", 12, 350000)));

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> reglas.pagar("f12", MedioPago.TARJETA, " "));

		assertEquals(400, ex.getStatusCode().value());
		verify(facturas, never()).save(any());
	}

	@Test
	void ser11_laBandejaDelCajeroTraeSoloLasPendientes() {
		when(facturas.findByEstadoOrderByNumeroAsc(EstadoFactura.PENDIENTE))
				.thenReturn(List.of(pendiente("f12", 12, 350000), pendiente("f13", 13, 90000)));

		List<Factura> lista = reglas.pendientes();

		assertEquals(2, lista.size());
		assertTrue(lista.stream().allMatch(f -> f.getEstado() == EstadoFactura.PENDIENTE));
	}

	// ---------------------------------------------------------------- SER-12

	@Test
	void ac012_1_cierreDelDiaConTotalesPorMedioDePago() {
		when(facturas.findByEstadoOrderByNumeroAsc(EstadoFactura.PAGADA)).thenReturn(List.of(
				pagada("a", 20, 100000, MedioPago.EFECTIVO, "cajero", LocalDateTime.of(2026, 11, 12, 9, 0)),
				pagada("b", 21, 50000, MedioPago.EFECTIVO, "cajero", LocalDateTime.of(2026, 11, 12, 11, 30)),
				pagada("c", 22, 200000, MedioPago.TARJETA, "cajero", LocalDateTime.of(2026, 11, 12, 18, 45)),
				// otro día y otro cajero: no entran en este cierre
				pagada("d", 23, 70000, MedioPago.EFECTIVO, "cajero", LocalDateTime.of(2026, 11, 11, 23, 59)),
				pagada("e", 24, 40000, MedioPago.TRANSFERENCIA, "caja2", LocalDateTime.of(2026, 11, 12, 10, 0))));

		CierreCaja c = reglas.cierre(LocalDate.of(2026, 11, 12), "cajero");

		assertEquals(LocalDate.of(2026, 11, 12), c.getFecha());
		assertEquals("cajero", c.getCajero());
		assertEquals(150000, c.getEfectivo());
		assertEquals(200000, c.getTarjeta());
		assertEquals(0, c.getTransferencia());
		assertEquals(350000, c.getTotal());
		assertEquals(3, c.getFacturas());
		assertEquals(3, c.getDetalle().size());
	}

	@Test
	void ac012_2_unDiaSinPagosDaTodoEnCero() {
		when(facturas.findByEstadoOrderByNumeroAsc(EstadoFactura.PAGADA)).thenReturn(List.of(
				pagada("a", 20, 100000, MedioPago.EFECTIVO, "cajero", LocalDateTime.of(2026, 11, 12, 9, 0))));

		CierreCaja c = reglas.cierre(LocalDate.of(2026, 11, 15), "cajero");

		assertEquals(0, c.getEfectivo());
		assertEquals(0, c.getTarjeta());
		assertEquals(0, c.getTransferencia());
		assertEquals(0, c.getTotal());
		assertEquals(0, c.getFacturas());
	}

	@Test
	void ser12_sinFechaNoHayCierre() {
		ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> reglas.cierre(null, "cajero"));
		assertEquals(400, ex.getStatusCode().value());
	}

	// ---------------------------------------------------------------- ayudas

	private static Factura pendiente(String id, long numero, double total) {
		Factura f = new Factura();
		f.setId(id);
		f.setNumero(numero);
		f.setTotal(total);
		f.setFecha(LocalDateTime.of(2026, 11, 12, 8, 0));
		f.setEstado(EstadoFactura.PENDIENTE);
		return f;
	}

	private static Factura pagada(String id, long numero, double total, MedioPago medio, String cajero, LocalDateTime cuando) {
		Factura f = pendiente(id, numero, total);
		Pago p = new Pago();
		p.setMedio(medio);
		p.setValor(total);
		p.setFecha(cuando);
		p.setCajero(cajero);
		f.setPago(p);
		f.setEstado(EstadoFactura.PAGADA);
		return f;
	}
}
