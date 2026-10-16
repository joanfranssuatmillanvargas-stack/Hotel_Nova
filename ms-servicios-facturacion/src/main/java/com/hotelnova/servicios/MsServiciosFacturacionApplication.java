package com.hotelnova.servicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "HotelNova - ms-servicios-facturacion", version = "1.0",
		description = "Catálogo de servicios adicionales, consumos por estadía, facturas con IVA, pagos y reportes"))
@SpringBootApplication
public class MsServiciosFacturacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsServiciosFacturacionApplication.class, args);
	}
}
