package com.hotelnova.web.controlador;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hotelnova.web.modelo.Rol;
import com.hotelnova.web.modelo.UsuarioSesion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * PLA-02 y PLA-09: exige iniciar sesión y deja entrar a cada sección solo a los roles que corresponden
 * (REQ-HN-004, 005; ADR-001). El administrador conserva el acceso del Release 1 a recepción, servicios y
 * limpieza; las secciones de los 5 roles nuevos son exclusivas de cada uno (matriz de permisos).
 */
@Component
public class FiltroSesion implements HandlerInterceptor {

	private static final Map<String, Set<Rol>> PERMISOS = Map.ofEntries(
			Map.entry("/admin", Set.of(Rol.ADMINISTRADOR)),
			Map.entry("/recepcion", Set.of(Rol.RECEPCIONISTA, Rol.ADMINISTRADOR)),
			Map.entry("/huesped", Set.of(Rol.HUESPED)),
			Map.entry("/servicios", Set.of(Rol.SERVICIOS, Rol.ADMINISTRADOR)),
			Map.entry("/limpieza", Set.of(Rol.LIMPIEZA, Rol.ADMINISTRADOR)),
			// Release 2
			Map.entry("/gerencia", Set.of(Rol.GERENTE)),
			Map.entry("/caja", Set.of(Rol.CAJERO)),
			Map.entry("/mantenimiento", Set.of(Rol.MANTENIMIENTO)),
			Map.entry("/supervision", Set.of(Rol.SUPERVISOR_LIMPIEZA)),
			Map.entry("/agente", Set.of(Rol.AGENTE_RESERVAS)));

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession sesion = request.getSession(false);
		UsuarioSesion yo = sesion == null ? null : (UsuarioSesion) sesion.getAttribute("yo");
		if (yo == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return false;
		}
		String ruta = request.getRequestURI().substring(request.getContextPath().length());
		for (Map.Entry<String, Set<Rol>> p : PERMISOS.entrySet()) {
			if ((ruta.equals(p.getKey()) || ruta.startsWith(p.getKey() + "/")) && !p.getValue().contains(yo.getRol())) {
				response.sendRedirect(request.getContextPath() + yo.getRol().getInicio());
				return false;
			}
		}
		return true;
	}
}
