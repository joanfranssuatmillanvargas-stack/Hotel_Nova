package com.hotelnova.web.modelo;

/** Resultado de consultar un microservicio desde la aplicación web. */
public class EstadoMicroservicio {

	private final String nombre;
	private final String url;
	private final String recurso;
	private final boolean activo;
	private final int registros;
	private final String mensaje;

	public EstadoMicroservicio(String nombre, String url, String recurso, boolean activo, int registros, String mensaje) {
		this.nombre = nombre;
		this.url = url;
		this.recurso = recurso;
		this.activo = activo;
		this.registros = registros;
		this.mensaje = mensaje;
	}

	public String getNombre() {
		return nombre;
	}

	public String getUrl() {
		return url;
	}

	public String getRecurso() {
		return recurso;
	}

	public boolean isActivo() {
		return activo;
	}

	public int getRegistros() {
		return registros;
	}

	public String getMensaje() {
		return mensaje;
	}

	public String getSwagger() {
		return url + "/swagger-ui.html";
	}
}
