package com.hotelnova.habitaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "HotelNova - ms-habitaciones", version = "1.0",
		description = "Habitaciones, tipos, tarifas por temporada, estado y flujo de limpieza"))
@SpringBootApplication
public class MsHabitacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsHabitacionesApplication.class, args);
	}
}
