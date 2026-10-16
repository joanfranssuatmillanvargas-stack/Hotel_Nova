package com.hotelnova.web.controlador;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Activa el filtro de sesión en todas las páginas menos login, registro y archivos estáticos. */
@Configuration
public class ConfiguracionWeb implements WebMvcConfigurer {

	private final FiltroSesion filtro;

	public ConfiguracionWeb(FiltroSesion filtro) {
		this.filtro = filtro;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(filtro).addPathPatterns("/**")
				.excludePathPatterns("/login", "/registro", "/salir", "/css/**", "/img/**", "/error", "/favicon.ico");
	}
}
