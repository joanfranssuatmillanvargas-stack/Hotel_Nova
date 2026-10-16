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
 * PLA-02: exige iniciar sesión y deja entrar a cada sección solo a los roles que corresponden.
 * El administrador puede entrar a todas las secciones del personal, pero no a la del huésped.
 */
@Component
public class FiltroSesion implements HandlerInterceptor {

	private static final Map<String, Set<Rol>> PERMISOS = Map.of(
			"/admin", Set.of(Rol.ADMINISTRADOR),
			"/recepcion", Set.of(Rol.RECEPCIONISTA, Rol.ADMINISTRADOR),
			"/huesped", Set.of(Rol.HUESPED),
			"/servicios", Set.of(Rol.SERVICIOS, Rol.ADMINISTRADOR),
			"/limpieza", Set.of(Rol.LIMPIEZA, Rol.ADMINISTRADOR));

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
