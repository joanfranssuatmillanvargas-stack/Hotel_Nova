package com.hotelnova.web.controlador;

import static com.hotelnova.web.repositorio.microservicios_repositorio.HABITACIONES;
import static com.hotelnova.web.repositorio.microservicios_repositorio.RESERVAS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.SERVICIOS;
import static com.hotelnova.web.repositorio.microservicios_repositorio.consulta;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotelnova.web.modelo.Rol;
import com.hotelnova.web.modelo.UsuarioSesion;
import com.hotelnova.web.repositorio.cuentas_repositorio;
import com.hotelnova.web.repositorio.microservicios_repositorio;

import jakarta.servlet.http.HttpSession;

/** Pantallas del administrador: panel, habitaciones y tarifas, servicios, usuarios y reportes. */
@Controller
@RequestMapping("/admin")
public class admin_web {

	private final microservicios_repositorio ms;
	private final cuentas_repositorio cuentas;

	public admin_web(microservicios_repositorio ms, cuentas_repositorio cuentas) {
		this.ms = ms;
		this.cuentas = cuentas;
	}

	// ======================= Panel (HAB-09) =======================

	@GetMapping
	public String panel(Model model) {
		model.addAttribute("servicios", ms.estados());
		try {
			model.addAttribute("ocupacion", ms.objeto(HABITACIONES, "/api/habitaciones/ocupacion"));
			model.addAttribute("sinDatos", ms.lista(HABITACIONES, "/api/tipos").isEmpty());
		} catch (ResponseStatusException ex) {
			model.addAttribute("ocupacion", null);
			model.addAttribute("sinDatos", false);
		}
		return "index";
	}

	/** Carga tipos, habitaciones, una temporada y servicios de ejemplo para probar el sistema. */
	@PostMapping("/datos-ejemplo")
	public String datosEjemplo(RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin", () -> {
			if (!ms.lista(HABITACIONES, "/api/tipos").isEmpty()) {
				return "Ya hay datos registrados: no se cargaron datos de ejemplo";
			}
			String[][] tipos = { { "Sencilla", "Una cama sencilla, baño privado", "1", "120000" },
					{ "Doble", "Dos camas o una cama doble", "2", "180000" },
					{ "Suite", "Cama king, sala y jacuzzi", "4", "350000" } };
			int piso = 1;
			for (String[] t : tipos) {
				Map<String, Object> tipo = ms.post(HABITACIONES, "/api/tipos",
						Map.of("nombre", t[0], "descripcion", t[1], "capacidad", t[2], "tarifaBase", t[3]));
				for (int i = 1; i <= 3; i++) {
					ms.post(HABITACIONES, "/api/habitaciones",
							Map.of("numero", String.valueOf(piso * 100 + i), "piso", piso, "tipoId", tipo.get("id")));
				}
				int anio = LocalDate.now().getYear();
				ms.post(HABITACIONES, "/api/temporadas", Map.of("nombre", "Temporada alta " + anio, "tipoId", tipo.get("id"),
						"inicio", anio + "-12-15", "fin", (anio + 1) + "-01-15",
						"tarifa", Math.round(Double.parseDouble(t[3]) * 1.3)));
				piso++;
			}
			Object[][] servicios = { { "Desayuno buffet", "RESTAURANTE", 25000 }, { "Almuerzo ejecutivo", "RESTAURANTE", 32000 },
					{ "Lavado de ropa (bolsa)", "LAVANDERIA", 18000 }, { "Planchado por prenda", "LAVANDERIA", 5000 },
					{ "Parqueadero carro (noche)", "PARQUEADERO", 15000 }, { "Parqueadero moto (noche)", "PARQUEADERO", 8000 },
					{ "Masaje relajante 60 min", "SPA", 90000 }, { "Sauna y turco", "SPA", 40000 } };
			for (Object[] s : servicios) {
				ms.post(SERVICIOS, "/api/servicios", Map.of("nombre", s[0], "categoria", s[1], "precio", s[2]));
			}
			return "Datos de ejemplo cargados: 3 tipos, 9 habitaciones, 3 temporadas y 8 servicios";
		});
	}

	// ======================= Habitaciones, tipos y temporadas (HAB-01 a HAB-08) =======================

	@GetMapping("/habitaciones")
	public String habitaciones(@RequestParam(name = "estado", required = false) String estado,
			@RequestParam(name = "tipo", required = false) String tipoId,
			@RequestParam(name = "editarTipo", required = false) String editarTipo,
			@RequestParam(name = "editarHab", required = false) String editarHab,
			@RequestParam(name = "editarTemp", required = false) String editarTemp, Model model) {
		List<Map<String, Object>> tipos = ms.lista(HABITACIONES, "/api/tipos");
		Map<String, Object> nombresTipo = new LinkedHashMap<>();
		tipos.forEach(t -> nombresTipo.put(Acciones.texto(t.get("id")), t.get("nombre")));
		model.addAttribute("tipos", tipos);
		model.addAttribute("nombresTipo", nombresTipo);
		model.addAttribute("habitaciones", ms.lista(HABITACIONES, "/api/habitaciones" + consulta("estado", estado, "tipoId", tipoId)));
		model.addAttribute("temporadas", ms.lista(HABITACIONES, "/api/temporadas"));
		model.addAttribute("filtroEstado", estado);
		model.addAttribute("filtroTipo", tipoId);
		model.addAttribute("tipoForm", Acciones.vacio(editarTipo) ? Map.of() : ms.objeto(HABITACIONES, "/api/tipos/" + editarTipo));
		model.addAttribute("habForm", Acciones.vacio(editarHab) ? Map.of() : ms.objeto(HABITACIONES, "/api/habitaciones/" + editarHab));
		model.addAttribute("tempForm", Acciones.vacio(editarTemp) ? Map.of() : ms.objeto(HABITACIONES, "/api/temporadas/" + editarTemp));
		return "admin/habitaciones";
	}

	@PostMapping("/tipos")
	public String guardarTipo(@RequestParam Map<String, String> form, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin/habitaciones", () -> {
			Map<String, Object> datos = Acciones.cuerpo(form, "nombre", "descripcion", "capacidad", "tarifaBase");
			if (Acciones.vacio(form.get("id"))) {
				ms.post(HABITACIONES, "/api/tipos", datos);
			} else {
				ms.put(HABITACIONES, "/api/tipos/" + form.get("id"), datos);
			}
			return "Tipo de habitación " + form.get("nombre") + " guardado";
		});
	}

	@PostMapping("/tipos/{id}/eliminar")
	public String eliminarTipo(@PathVariable("id") String id, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin/habitaciones", () -> {
			ms.delete(HABITACIONES, "/api/tipos/" + id);
			return "Tipo de habitación eliminado";
		});
	}

	@PostMapping("/habitaciones")
	public String guardarHabitacion(@RequestParam Map<String, String> form, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin/habitaciones", () -> {
			Map<String, Object> datos = Acciones.cuerpo(form, "numero", "piso", "tipoId");
			if (Acciones.vacio(form.get("id"))) {
				ms.post(HABITACIONES, "/api/habitaciones", datos);
			} else {
				ms.put(HABITACIONES, "/api/habitaciones/" + form.get("id"), datos);
			}
			return "Habitación " + form.get("numero") + " guardada";
		});
	}

	@PostMapping("/habitaciones/{id}/eliminar")
	public String eliminarHabitacion(@PathVariable("id") String id, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin/habitaciones", () -> {
			ms.delete(HABITACIONES, "/api/habitaciones/" + id);
			return "Habitación eliminada";
		});
	}

	@PostMapping("/habitaciones/{id}/estado")
	public String estadoHabitacion(@PathVariable("id") String id, @RequestParam("estado") String estado, HttpSession sesion,
			RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/admin/habitaciones", () -> {
			Map<String, Object> h = ms.patch(HABITACIONES, "/api/habitaciones/" + id + "/estado",
					Map.of("estado", estado, "usuario", yo.getUsuario()));
			return "La habitación " + h.get("numero") + " quedó en estado " + h.get("estado");
		});
	}

	@PostMapping("/temporadas")
	public String guardarTemporada(@RequestParam Map<String, String> form, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin/habitaciones", () -> {
			Map<String, Object> datos = Acciones.cuerpo(form, "nombre", "tipoId", "inicio", "fin", "tarifa");
			if (Acciones.vacio(form.get("id"))) {
				ms.post(HABITACIONES, "/api/temporadas", datos);
			} else {
				ms.put(HABITACIONES, "/api/temporadas/" + form.get("id"), datos);
			}
			return "Temporada " + form.get("nombre") + " guardada";
		});
	}

	@PostMapping("/temporadas/{id}/eliminar")
	public String eliminarTemporada(@PathVariable("id") String id, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin/habitaciones", () -> {
			ms.delete(HABITACIONES, "/api/temporadas/" + id);
			return "Temporada eliminada";
		});
	}

	// ======================= Servicios adicionales (SER-01, SER-02) =======================

	@GetMapping("/servicios")
	public String servicios(@RequestParam(name = "editar", required = false) String editar, Model model) {
		model.addAttribute("servicios", ms.lista(SERVICIOS, "/api/servicios"));
		model.addAttribute("form", Acciones.vacio(editar) ? Map.of() : ms.objeto(SERVICIOS, "/api/servicios/" + editar));
		return "admin/servicios";
	}

	@PostMapping("/servicios")
	public String guardarServicio(@RequestParam Map<String, String> form, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin/servicios", () -> {
			Map<String, Object> datos = Acciones.cuerpo(form, "nombre", "categoria", "precio");
			if (Acciones.vacio(form.get("id"))) {
				ms.post(SERVICIOS, "/api/servicios", datos);
			} else {
				ms.put(SERVICIOS, "/api/servicios/" + form.get("id"), datos);
			}
			return "Servicio " + form.get("nombre") + " guardado";
		});
	}

	@PostMapping("/servicios/{id}/activo")
	public String activoServicio(@PathVariable("id") String id, @RequestParam("valor") boolean valor, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin/servicios", () -> {
			Map<String, Object> s = ms.patch(SERVICIOS, "/api/servicios/" + id + "/activo" + consulta("valor", valor), null);
			return s.get("nombre") + (valor ? " activado" : " desactivado");
		});
	}

	@PostMapping("/servicios/{id}/eliminar")
	public String eliminarServicio(@PathVariable("id") String id, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin/servicios", () -> {
			ms.delete(SERVICIOS, "/api/servicios/" + id);
			return "Servicio eliminado";
		});
	}

	// ======================= Usuarios (PLA-03) =======================

	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", cuentas.listar());
		model.addAttribute("roles", Rol.values());
		return "admin/usuarios";
	}

	@PostMapping("/usuarios")
	public String crearUsuario(@RequestParam Map<String, String> form, RedirectAttributes flash) {
		return Acciones.hacer(flash, "/admin/usuarios", () -> {
			Rol rol = Acciones.vacio(form.get("rol")) ? null : Rol.valueOf(form.get("rol"));
			cuentas.crear(form.get("usuario"), form.get("nombre"), rol, form.get("documento"), form.get("clave"));
			return "Usuario " + form.get("usuario") + " creado";
		});
	}

	@PostMapping("/usuarios/{id}/activo")
	public String activoUsuario(@PathVariable("id") String id, HttpSession sesion, RedirectAttributes flash) {
		UsuarioSesion yo = (UsuarioSesion) sesion.getAttribute("yo");
		return Acciones.hacer(flash, "/admin/usuarios", () -> {
			cuentas.cambiarActivo(id, yo.getUsuario());
			return "Usuario actualizado";
		});
	}

	// ======================= Reportes (RES-10, SER-10, HAB-14) =======================

	@GetMapping("/reportes")
	public String reportes(@RequestParam(name = "desde", required = false) String desde,
			@RequestParam(name = "hasta", required = false) String hasta,
			@RequestParam(name = "habitacionId", required = false) String habitacionId, Model model) {
		LocalDate hoy = LocalDate.parse(Acciones.texto(model.getAttribute("hoy")));
		String d = Acciones.vacio(desde) ? hoy.withDayOfMonth(1).toString() : desde;
		String h = Acciones.vacio(hasta) ? hoy.withDayOfMonth(hoy.lengthOfMonth()).toString() : hasta;
		model.addAttribute("desde", d);
		model.addAttribute("hasta", h);
		model.addAttribute("habitacionId", habitacionId);
		model.addAttribute("reservas", ms.objeto(RESERVAS, "/api/reservas/reporte" + consulta("desde", d, "hasta", h)));
		model.addAttribute("ingresos", ms.objeto(SERVICIOS, "/api/facturas/reporte" + consulta("desde", d, "hasta", h)));
		model.addAttribute("novedades", ms.lista(HABITACIONES,
				"/api/novedades" + consulta("habitacionId", habitacionId, "desde", d, "hasta", h)));
		model.addAttribute("habitaciones", ms.lista(HABITACIONES, "/api/habitaciones"));
		model.addAttribute("facturas", ms.lista(SERVICIOS, "/api/facturas"));
		return "admin/reportes";
	}
}
