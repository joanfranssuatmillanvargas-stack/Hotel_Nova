package com.hotelnova.web.modelo;

/** Roles del sistema (10 desde el Release 2, REQ-HN-001). Cada rol tiene su página de inicio. */
public enum Rol {

	ADMINISTRADOR("Administrador", "/admin"),
	RECEPCIONISTA("Recepcionista", "/recepcion"),
	HUESPED("Huésped", "/huesped"),
	SERVICIOS("Personal de servicios", "/servicios"),
	LIMPIEZA("Personal de limpieza", "/limpieza"),
	// Release 2 (PLA-08, ADR-001)
	GERENTE("Gerente", "/gerencia"),
	CAJERO("Cajero", "/caja"),
	MANTENIMIENTO("Técnico de mantenimiento", "/mantenimiento"),
	SUPERVISOR_LIMPIEZA("Supervisor de limpieza", "/supervision"),
	AGENTE_RESERVAS("Agente de reservas", "/agente");

	private final String etiqueta;
	private final String inicio;

	Rol(String etiqueta, String inicio) {
		this.etiqueta = etiqueta;
		this.inicio = inicio;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public String getInicio() {
		return inicio;
	}
}
