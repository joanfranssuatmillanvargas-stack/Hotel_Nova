package com.hotelnova.reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "HotelNova - ms-reservas", version = "1.0",
		description = "Reservas, disponibilidad, check-in y check-out; se comunica por REST con los otros 3 microservicios"))
@SpringBootApplication
public class MsReservasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsReservasApplication.class, args);
	}
}
