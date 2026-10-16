package com.hotelnova.huespedes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "HotelNova - ms-huespedes", version = "1.0",
		description = "Huéspedes, acompañantes e historial de estadías"))
@SpringBootApplication
public class MsHuespedesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsHuespedesApplication.class, args);
	}
}
