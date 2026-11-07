# Roles del sistema HotelNova (10 perfiles de usuario)

**Tarjeta:** D02 · **Responsable:** Jaider Javier Jaimes (PO) · **Revisa:** Joan Franssuat Millán Vargas
**Versión:** 1.0 · Sprint 0 (28/10/2026 – 03/11/2026)

HotelNova pasa de 5 a 10 perfiles de usuario. Los 5 roles existentes se mantienen; los 5 nuevos
separan responsabilidades que hoy están concentradas en el Administrador y en el Recepcionista, y
cierran flujos que quedaban abiertos (por ejemplo, una habitación en mantenimiento no tenía quién la
reparara y liberara).

Estos son roles de **usuario del sistema** (actores), no roles de Scrum.

## 1. Los 10 roles

| # | Rol | Código | Estado | Módulos que usa | Qué lo distingue |
|---|-----|--------|--------|-----------------|------------------|
| 1 | Administrador | `ADMIN` | Existente | Todos (configuración) | Crea usuarios y les asigna rol; mantiene tipos, habitaciones, temporadas y catálogo de servicios |
| 2 | Recepcionista | `RECEPCIONISTA` | Existente | Reservas, Huéspedes, Habitaciones | Registra huéspedes y acompañantes, crea/modifica reservas, hace check-in y check-out |
| 3 | Huésped | `HUESPED` | Existente | Reservas (propias), Huéspedes (propios) | Se registra solo, reserva en línea, cancela y ve sus consumos; solo ve lo suyo |
| 4 | Personal de servicios | `SERVICIOS` | Existente | Servicios y facturación | Carga consumos (restaurante, lavandería, parqueadero, spa) a estadías en curso |
| 5 | Personal de limpieza (camarera) | `LIMPIEZA` | Existente | Habitaciones | Ve sus habitaciones por limpiar, las marca limpias y reporta daños (desde el celular) |
| 6 | **Gerente** | `GERENTE` | **Nuevo** | Reportes de los 4 microservicios; Temporadas | Ve el tablero gerencial (ocupación, tarifa promedio, ingresos) y **aprueba las tarifas de temporada**; no opera reservas ni cobra |
| 7 | **Cajero** | `CAJERO` | **Nuevo** | Servicios y facturación | **Registra los pagos**, anula consumos con motivo y hace el **cierre de caja diario** por medio de pago |
| 8 | **Técnico de mantenimiento** | `MANTENIMIENTO` | **Nuevo** | Habitaciones (novedades) | Ve las habitaciones en MANTENIMIENTO con su daño, **registra la reparación** y la habitación pasa a LIMPIEZA |
| 9 | **Supervisor de limpieza (ama de llaves)** | `SUPERVISOR_LIMPIEZA` | **Nuevo** | Habitaciones, Usuarios (solo camareras) | **Asigna las habitaciones pendientes a cada camarera**, reasigna y ve el avance y el historial del equipo |
| 10 | **Agente de reservas (call center)** | `AGENTE_RESERVAS` | **Nuevo** | Reservas, Huéspedes | Crea, modifica y cancela reservas **por teléfono o correo**, registrando el **canal**; no hace check-in, check-out ni cobros |

## 2. Qué cambia con los roles nuevos

### 6 · Gerente
- **Necesidad:** la gerencia quiere ver cómo va el hotel sin entrar a configurar nada ni arriesgarse a modificar datos.
- **Funcionalidad nueva:**
  - Tablero gerencial con ocupación, tarifa promedio por noche vendida e ingresos por periodo y por categoría.
  - Aprobación de temporadas: una temporada que crea el Administrador queda **PENDIENTE** y solo aplica su tarifa cuando el Gerente la aprueba.
- **Permisos:** lectura de los reportes de los 4 microservicios; aprobar o rechazar temporadas. No puede crear reservas, cobrar ni editar usuarios.

### 7 · Cajero
- **Necesidad:** separar quien atiende al huésped (recepción) de quien maneja el dinero (caja), como exige un control interno básico.
- **Funcionalidad nueva:**
  - Bandeja de facturas PENDIENTES.
  - Registro del pago con su medio: efectivo, tarjeta o transferencia.
  - Anulación de consumos con motivo.
  - **Cierre de caja del día** con totales por medio de pago y por cajero.
- **Permisos:** solo el módulo de servicios y facturación. La Recepcionista sigue generando la factura en el check-out, pero **ya no registra pagos**.

### 8 · Técnico de mantenimiento
- **Necesidad:** hoy una habitación con un daño queda en MANTENIMIENTO y nadie tiene cómo devolverla al servicio.
- **Funcionalidad nueva:**
  - Lista de habitaciones en MANTENIMIENTO con la novedad que las envió ahí.
  - Registro de la reparación (qué se hizo y fecha). La habitación pasa a **LIMPIEZA**: se limpia antes de volver a ofrecerse.
- **Permisos:** solo las novedades y el estado de las habitaciones en mantenimiento.

### 9 · Supervisor de limpieza (ama de llaves)
- **Necesidad:** con varias camareras, alguien debe repartir el trabajo y controlar que se cumpla.
- **Funcionalidad nueva:**
  - **Asignar** cada habitación pendiente a una camarera, que desde ese momento solo ve **las suyas**.
  - Reasignar habitaciones.
  - Ver el avance del día y el historial por camarera.
- **Permisos:** habitaciones en LIMPIEZA y lista de usuarios con rol LIMPIEZA (solo consulta).

### 10 · Agente de reservas (call center)
- **Necesidad:** muchas reservas llegan por teléfono, WhatsApp o correo, y no conviene que las tome recepción mientras atiende a quien llega.
- **Funcionalidad nueva:**
  - Reservas con **canal** (TELÉFONO, CORREO, WHATSAPP, WEB, MOSTRADOR).
  - Registro rápido del huésped si no existe.
  - Modificar y cancelar reservas.
  - Consultar disponibilidad y tarifa.
- **Permisos:** reservas y huéspedes. No hace check-in ni check-out, no carga consumos ni cobra.

## 3. Matriz de permisos (quién puede hacer qué)

L = consultar · E = crear/editar · A = aprobar/decidir · — = sin acceso

| Funcionalidad | ADMIN | RECEP. | HUÉSP. | SERV. | LIMP. | GER. | CAJ. | MANT. | SUP. L. | AG. RES. |
|---------------|:-----:|:------:|:------:|:-----:|:-----:|:----:|:----:|:-----:|:-------:|:--------:|
| Usuarios y roles | E | — | — | — | — | L | — | — | L (camareras) | — |
| Tipos, habitaciones | E | L | — | — | — | L | — | — | L | L |
| Temporadas y tarifas | E | — | — | — | — | **A** | — | — | — | L |
| Estado de habitaciones | E | E | — | — | — | L | — | — | L | L |
| Huéspedes | L | E | E (propio) | — | — | — | — | — | — | E |
| Reservas | L | E | E (propias) | — | — | L | — | — | — | E |
| Check-in / check-out | — | E | — | — | — | — | — | — | — | — |
| Catálogo de servicios | E | — | — | L | — | L | L | — | — | — |
| Consumos | L | L | L (propios) | E | — | — | E (anular) | — | — | — |
| Facturas y pagos | L | E (genera) | L (propias) | — | — | L | **E (cobra)** | — | — | — |
| Cierre de caja | — | — | — | — | — | L | **E** | — | — | — |
| Limpieza (marcar limpia, daños) | — | — | — | — | E (asignadas) | — | — | — | L | — |
| Asignar habitaciones a camareras | — | — | — | — | — | — | — | — | **E** | — |
| Reparaciones | — | — | — | — | — | L | — | **E** | — | — |
| Reportes | L | — | — | — | — | **L (todos)** | L (caja) | — | L (limpieza) | — |

## 4. Usuarios de prueba propuestos

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Administrador | admin | admin123 |
| Recepcionista | recepcion | recepcion123 |
| Personal de servicios | servicios | servicios123 |
| Personal de limpieza | limpieza | limpieza123 |
| Huésped | se registra en «Crear cuenta» | la que elija |
| Gerente | gerente | gerente123 |
| Cajero | cajero | cajero123 |
| Técnico de mantenimiento | mantenimiento | mantenimiento123 |
| Supervisor de limpieza | supervisor | supervisor123 |
| Agente de reservas | agente | agente123 |

## 5. Dónde entra cada rol en el Product Backlog

| Rol nuevo | PBI | Sprint |
|-----------|-----|--------|
| Los 5 (enum de roles, permisos por ruta, usuarios de prueba) | PBI-02 | 1 |
| Técnico de mantenimiento | PBI-03 | 1 |
| Cajero | PBI-04 | 2 |
| Agente de reservas | PBI-05 | 2 |
| Supervisor de limpieza | PBI-06 | 2 |
| Gerente | PBI-07 | 3 |

El detalle de cada rol (requisitos EARS y criterios Given/When/Then) se escribe en `spec.md` durante
el Sprint 1, como manda SDD: el backlog guarda la intención, no la especificación completa.
