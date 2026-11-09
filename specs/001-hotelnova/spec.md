# Especificación: HotelNova con 10 roles · Versión 1.0 (para la Puerta 1)

**PBI:** PBI-01 · **Tarjetas:** D08 (v0.1), D09 (v0.2), D10 (v1.0)
**Redacta:** Joan Franssuat Millán Vargas · **Aprueba (PO):** Jaider Javier Jaimes
**Release:** 2 (tercer corte) · R2-Sprint 1 a 3 (04/11 – 24/11/2026)

> Esta spec dice **qué** debe hacer HotelNova y **para quién**. La tecnología (Spring Boot, MongoDB, rutas,
> clases) va en `plan.md`, como pide la constitución.

## 1. Contexto y objetivo

HotelNova ya opera con 4 microservicios, una aplicación web y 5 perfiles de usuario (Release 1, 52 historias,
desplegado en Render). En la operación real de un hotel faltan perfiles: nadie devuelve al servicio una
habitación dañada, la recepcionista recibe el dinero, no se sabe por qué canal llegan las reservas, el trabajo de
limpieza no se reparte y la gerencia no tiene dónde ver los indicadores.

**Product Goal:** que el hotel opere su día completo en HotelNova, desde la reserva por teléfono hasta el cierre
de caja, con 10 perfiles de usuario que solo ven lo que les corresponde, sin reservas cruzadas, sin habitaciones
dañadas ofrecidas a huéspedes y sin consumos sin cobrar.

## 2. Actores

| Actor | Estado | Qué hace en HotelNova |
|-------|--------|------------------------|
| Administrador | Existente | Configura habitaciones, tipos, temporadas, servicios y usuarios |
| Recepcionista | Existente | Huéspedes, reservas, check-in, check-out y factura |
| Huésped | Existente | Se registra, reserva en línea y ve sus consumos |
| Personal de servicios | Existente | Carga consumos a las estadías |
| Personal de limpieza (camarera) | Existente | Limpia habitaciones y reporta daños |
| **Gerente** | Nuevo | Consulta indicadores y aprueba tarifas de temporada; no opera |
| **Cajero** | Nuevo | Cobra las facturas y hace el cierre de caja |
| **Técnico de mantenimiento** | Nuevo | Repara habitaciones dañadas y las devuelve al servicio |
| **Supervisor de limpieza** | Nuevo | Reparte las habitaciones por limpiar entre las camareras |
| **Agente de reservas** | Nuevo | Toma reservas por teléfono, correo o WhatsApp |

La matriz de permisos completa está en `docs/sprint-0/roles-del-sistema.md` y forma parte de esta spec.

## 3. Alcance y no-objetivos

**Dentro del alcance (Release 2):** los 5 roles nuevos con su acceso propio y las 14 historias de la sección 5.

**No-objetivos (no se hacen en este release):**
- Pasarelas de pago reales o datáfonos: el cajero **registra** el pago, no lo procesa.
- Facturación electrónica ante la DIAN.
- Aplicación móvil nativa: las pantallas son web y se usan desde el celular.
- Inicio de sesión con cuentas externas (Google, Microsoft) o recuperación de contraseña por correo.
- Mensajes por SMS o WhatsApp. El único correo es el de RES-13, que es objetivo extendido.
- Cambiar las reglas del Release 1 que no menciona esta spec.

## 4. Glosario

| Término | Significado |
|---------|-------------|
| Inicio del rol | Página a la que llega cada usuario al iniciar sesión; cada rol tiene la suya |
| Estado de habitación | DISPONIBLE, OCUPADA, LIMPIEZA o MANTENIMIENTO (Release 1) |
| Novedad | Registro sobre una habitación: limpieza hecha, daño reportado o reparación hecha |
| Daño | Novedad que reporta el personal de limpieza y que manda la habitación a MANTENIMIENTO |
| Reparación | Novedad que registra el técnico y que pasa la habitación de MANTENIMIENTO a LIMPIEZA |
| Canal | Por dónde llegó una reserva: TELÉFONO, CORREO, WHATSAPP, WEB o MOSTRADOR |
| Cierre de caja | Totales del día por medio de pago (efectivo, tarjeta, transferencia), con número de facturas y cajero |
| Asignación | Habitación en LIMPIEZA que el supervisor le encarga a una camarera |
| Temporada pendiente | Temporada creada por el administrador que todavía no aprueba el gerente; no cambia el precio |

## 5. Historias de usuario (con INVEST)

| ID | PBI | Sprint | Historia | INVEST |
|----|-----|:------:|----------|--------|
| PLA-08 | PBI-02 | 1 | Como administrador quiero crear usuarios con los 5 roles nuevos para que cada empleado tenga su propio acceso | Independiente, pequeña, comprobable |
| PLA-09 | PBI-02 | 1 | Como usuario quiero que si abro una ruta de otro rol el sistema me devuelva a mi inicio para que nadie vea información que no le corresponde | Valiosa (Ley 1581), comprobable rol por rol |
| HAB-16 | PBI-03 | 1 | Como técnico de mantenimiento quiero ver las habitaciones en mantenimiento con el daño que las envió ahí para saber qué reparar primero | Independiente, pequeña |
| HAB-17 | PBI-03 | 1 | Como técnico quiero registrar la reparación de una habitación para que vuelva al servicio después de limpiarse | Pequeña, comprobable (borde: estado distinto) |
| SER-11 | PBI-04 | 2 | Como cajero quiero ver las facturas pendientes y registrar su pago para que el dinero quede registrado por quien lo recibe | Valiosa (control interno) |
| SER-12 | PBI-04 | 2 | Como cajero quiero generar el cierre de caja del día para que el dinero cuadre con lo facturado | Comprobable con cifras |
| RES-11 | PBI-05 | 2 | Como agente quiero crear reservas indicando el canal para saber de dónde llegan los clientes | Valiosa (gerencia) |
| RES-12 | PBI-05 | 2 | Como agente quiero registrar rápido a un huésped y modificar o cancelar sus reservas para atender la llamada sin pasarla a recepción | Negociable (qué campos son obligatorios) |
| HAB-18 | PBI-06 | 2 | Como supervisor quiero asignar cada habitación por limpiar a una camarera y reasignarla para repartir el trabajo | Comprobable (borde: reasignar) |
| HAB-19 | PBI-06 | 2 | Como camarera quiero ver solo las habitaciones que me asignaron para no cruzarme con otra | Pequeña |
| PLA-10 | PBI-07 | 3 | Como gerente quiero un tablero con ocupación, tarifa promedio e ingresos para decidir con datos | Valiosa, estimable |
| HAB-20 | PBI-07 | 3 | Como gerente quiero aprobar o rechazar las temporadas para que ninguna tarifa cambie sin autorización | Comprobable con fechas |
| PLA-11 | PBI-08 | 3 | Como equipo queremos la matriz de trazabilidad completa y los 10 roles en Render para tomar la decisión de release con evidencia | Comprobable |
| RES-13 | PBI-09 | Reserva | Como huésped quiero recibir un correo con el código de mi reserva para tener constancia | Objetivo extendido |

## 6. Reglas de negocio

| ID | Regla | Origen |
|----|-------|--------|
| RN-01 | Cada usuario tiene un solo rol; entra al inicio de su rol y solo ve el menú de ese rol | Administración del hotel (vigente desde el Release 1) |
| RN-02 | Quien abre una página de otro rol vuelve a su propio inicio sin ver datos de esa página | Seguridad y Ley 1581 de 2012 (constitución, principio 6) |
| RN-03 | Una habitación en MANTENIMIENTO no se ofrece para reservas y solo el técnico la devuelve al servicio | Operaciones (HAB-08 del Release 1) |
| RN-04 | Al reparar una habitación, pasa a LIMPIEZA y no a DISPONIBLE: se limpia antes de volver a ofrecerse | Operaciones (decisión 1 de la clarificación) |
| RN-05 | Solo el cajero registra pagos; la recepcionista genera la factura en el check-out | Control interno (gerencia) |
| RN-06 | Una factura PAGADA no se cobra dos veces | Contabilidad (vigente desde el Release 1) |
| RN-07 | Toda reserva guarda el canal por el que llegó y quién la tomó | Gerencia (medir los canales) |
| RN-08 | No se aceptan reservas con fechas cruzadas en la misma habitación, venga de quien venga | Operaciones (RES-05 del Release 1) |
| RN-09 | Solo se asignan habitaciones en LIMPIEZA y solo a usuarios con rol de limpieza; cada camarera ve solo las suyas | Ama de llaves |
| RN-10 | Una temporada nueva queda PENDIENTE y no cambia el precio hasta que el gerente la aprueba | Gerencia |
| RN-11 | El gerente solo consulta: no crea, no edita y no cobra | Gerencia |
| RN-12 | Si falla el correo de confirmación, la reserva igual queda creada y el fallo se registra | Operaciones |

## 7. Requisitos funcionales (EARS)

| ID | Patrón | Requisito | Historia |
|----|--------|-----------|----------|
| REQ-HN-001 | Ubicuo | El sistema deberá manejar 10 roles (los 5 del Release 1 más Gerente, Cajero, Técnico de mantenimiento, Supervisor de limpieza y Agente de reservas) y permitir al administrador asignar cualquiera de ellos al crear un usuario. | PLA-08 |
| REQ-HN-002 | Evento | Cuando un usuario inicia sesión, el sistema deberá llevarlo al inicio de su rol y mostrarle solo el menú de ese rol. | PLA-08 |
| REQ-HN-003 | Evento | Cuando arranca la aplicación web, el sistema deberá crear el usuario de prueba de cada rol del personal que todavía no exista, sin modificar los usuarios que ya existen. | PLA-08 |
| REQ-HN-004 | No deseado | Si un usuario abre una página que no corresponde a su rol, entonces el sistema deberá devolverlo a su propio inicio sin mostrar datos de esa página. | PLA-09 |
| REQ-HN-005 | No deseado | Si alguien sin sesión abre una página interna, entonces el sistema deberá llevarlo al inicio de sesión. | PLA-09 |
| REQ-HN-006 | Evento | Cuando el técnico abre su inicio, el sistema deberá listar solo las habitaciones en MANTENIMIENTO, cada una con el último daño reportado: descripción, fecha y quién lo reportó. | HAB-16 |
| REQ-HN-007 | Evento | Cuando el técnico registra la reparación de una habitación en MANTENIMIENTO, el sistema deberá guardar qué se hizo, quién y cuándo, y pasar la habitación a LIMPIEZA. | HAB-17 |
| REQ-HN-008 | No deseado | Si la habitación no está en MANTENIMIENTO, entonces el sistema deberá rechazar la reparación indicando el estado actual de la habitación. | HAB-17 |
| REQ-HN-009 | Evento | Cuando el cajero registra el pago de una factura PENDIENTE con su medio de pago, el sistema deberá pasarla a PAGADA y guardar qué cajero la cobró. | SER-11 |
| REQ-HN-010 | No deseado | Si la factura ya está PAGADA, entonces el sistema deberá rechazar el pago. | SER-11 |
| REQ-HN-011 | Ubicuo | El sistema deberá permitir registrar pagos únicamente al rol Cajero. | SER-11 |
| REQ-HN-012 | Evento | Cuando el cajero genera el cierre de caja de un día, el sistema deberá mostrar el total por medio de pago, el número de facturas pagadas y el cajero. | SER-12 |
| REQ-HN-013 | Evento | Cuando el agente crea una reserva, el sistema deberá guardar el canal y el agente que la tomó, aplicando las mismas reglas de fechas cruzadas y mantenimiento. | RES-11 |
| REQ-HN-014 | Ubicuo | El sistema deberá permitir al agente registrar huéspedes y modificar o cancelar reservas, sin acceso a check-in, check-out ni pagos. | RES-12 |
| REQ-HN-015 | Evento | Cuando el supervisor asigna una habitación en LIMPIEZA a una camarera, el sistema deberá guardar la asignación; si la reasigna, la habitación deberá salir de la lista de la camarera anterior. | HAB-18 |
| REQ-HN-016 | No deseado | Si la habitación no está en LIMPIEZA o el usuario elegido no tiene rol de limpieza, entonces el sistema deberá rechazar la asignación. | HAB-18 |
| REQ-HN-017 | Ubicuo | El sistema deberá mostrar a cada camarera solo las habitaciones que tiene asignadas; las no asignadas solo las verá el supervisor. | HAB-19 |
| REQ-HN-018 | Evento | Cuando el gerente abre su inicio, el sistema deberá mostrar la ocupación del día, la tarifa promedio por noche vendida y los ingresos del periodo elegido por categoría. | PLA-10 |
| REQ-HN-019 | Ubicuo | El sistema deberá mostrar al gerente la información sin acciones para crear, editar ni cobrar. | PLA-10 |
| REQ-HN-020 | Evento | Cuando el administrador crea una temporada, el sistema deberá guardarla PENDIENTE y seguir usando la tarifa anterior hasta que el gerente la apruebe. | HAB-20 |
| REQ-HN-021 | Evento | Cuando el gerente aprueba una temporada PENDIENTE, el sistema deberá aplicar su tarifa; cuando la rechaza, deberá marcarla RECHAZADA sin aplicar su tarifa. | HAB-20 |
| REQ-HN-022 | Opcional | Donde esté activo el envío de correo, cuando se confirma una reserva de un huésped con correo, el sistema deberá enviarle el código de la reserva. | RES-13 |
| REQ-HN-023 | No deseado | Si el envío del correo falla, entonces el sistema deberá dejar la reserva creada y registrar el fallo. | RES-13 |

## 8. Requisitos no funcionales (medibles)

| ID | Requisito | Cómo se medirá |
|----|-----------|----------------|
| NFR-HN-001 | Las contraseñas de los usuarios nuevos se guardan cifradas con una sal propia; nunca en texto plano | Revisión de la colección de usuarios después de crear uno |
| NFR-HN-002 | Ningún rol ve datos personales de huéspedes que no necesita para su trabajo (matriz de permisos) | Prueba de acceso por ruta para los 10 roles (P022) |
| NFR-HN-003 | La pantalla del técnico se puede usar en un celular de 360 px de ancho sin desplazamiento horizontal | Prueba manual en modo móvil del navegador |
| NFR-HN-004 | Los 10 roles inician sesión en la URL pública del despliegue | Prueba P034 al cierre del R2-Sprint 3 |
| NFR-HN-005 | Cada requisito de esta spec tiene al menos una prueba en la matriz de trazabilidad | Revisión de la matriz en la Review del R2-Sprint 3 |

## 9. Criterios de aceptación (Given/When/Then)

Fechas de ejemplo en noviembre de 2026. Usuarios de prueba de `roles-del-sistema.md`, sección 4.

### R2-Sprint 1

| ID | Dado (Given) | Cuando (When) | Entonces (Then) |
|----|--------------|---------------|-----------------|
| AC-001.1 | El administrador está en "Usuarios y roles" | Abre la lista de roles para crear un usuario | Aparecen los 10 roles |
| AC-001.2 | El administrador crea el usuario `caja2` con rol Cajero | `caja2` inicia sesión | Entra al inicio del cajero |
| AC-002.1 | Existen `gerente`, `cajero`, `mantenimiento`, `supervisor` y `agente` | Cada uno inicia sesión con su contraseña de prueba | Cada uno llega a su propio inicio y su menú solo tiene opciones de su rol |
| AC-002.2 | Existe `recepcion` (rol del Release 1) | Inicia sesión | Sigue llegando al inicio de recepción, como antes |
| AC-003.1 | La base de usuarios ya tiene `admin`, `recepcion`, `servicios` y `limpieza` (como en Render) | Arranca la aplicación web | Se crean `gerente`, `cajero`, `mantenimiento`, `supervisor` y `agente`, y los 4 anteriores no cambian |
| AC-003.2 (borde) | Ya existe `gerente` y está desactivado | Arranca la aplicación web | No se crea otro `gerente` ni se reactiva el existente |
| AC-003.3 (borde) | La base de usuarios está vacía (instalación nueva) | Arranca la aplicación web | Se crean los 9 usuarios del personal (todos menos el huésped) |
| AC-004.1 | Sesión de `cajero` | Abre la página de reservas de recepción | Vuelve al inicio del cajero sin ver reservas |
| AC-004.2 | Sesión de `mantenimiento` | Abre la página de usuarios del administrador | Vuelve al inicio del técnico |
| AC-004.3 (borde) | Sesión de `admin` | Abre la página del técnico | Vuelve al inicio del administrador (la matriz no le da reparaciones) |
| AC-004.4 (borde) | Sesión de `limpieza` | Abre la página del supervisor de limpieza | Vuelve a su inicio de limpieza |
| AC-005.1 | No hay sesión | Se abre la página del técnico | Se muestra el inicio de sesión |
| AC-006.1 | La 204 está en MANTENIMIENTO por "grifo del baño goteando", reportado por `limpieza` el 05/11 a las 8:30; la 101 está DISPONIBLE | El técnico abre su inicio | Ve solo la 204, con esa descripción, la fecha y `limpieza` |
| AC-006.2 (borde) | La 305 tiene dos daños: 03/11 "bombillo fundido" y 06/11 "aire no enfría" | El técnico abre su inicio | La 305 aparece una sola vez, con "aire no enfría" |
| AC-006.3 | Ninguna habitación está en MANTENIMIENTO | El técnico abre su inicio | Ve el mensaje "No hay habitaciones en mantenimiento" |
| AC-007.1 | La 204 está en MANTENIMIENTO | El técnico registra "Se cambió el empaque del grifo" | La 204 pasa a LIMPIEZA, queda la reparación con el técnico, la fecha y lo que hizo, y la 204 aparece en la lista de limpieza |
| AC-007.2 | La 204 está en MANTENIMIENTO | El técnico registra la reparación sin escribir qué hizo | Se rechaza: "Falta la descripción de la reparación" |
| AC-008.1 | La 101 está DISPONIBLE | Se intenta registrar una reparación de la 101 | Se rechaza indicando que la 101 está DISPONIBLE y que solo se reparan habitaciones en MANTENIMIENTO |
| AC-008.2 (borde) | La 204 ya se reparó y está en LIMPIEZA | El técnico vuelve a registrar la reparación (doble clic) | Se rechaza y no queda una segunda reparación |

### R2-Sprint 2

| ID | Dado (Given) | Cuando (When) | Entonces (Then) |
|----|--------------|---------------|-----------------|
| AC-009.1 | Factura F-0012 PENDIENTE por $350.000 | El cajero registra el pago en EFECTIVO | F-0012 queda PAGADA con `cajero` como quien cobró |
| AC-010.1 | F-0012 ya está PAGADA | El cajero intenta cobrarla otra vez | Se rechaza: la factura ya está pagada |
| AC-011.1 | Sesión de `recepcion` | Busca cómo registrar un pago | No aparece la opción y la página del cajero la devuelve a recepción |
| AC-012.1 | El 12/11 hubo pagos en efectivo de $100.000 y $50.000 y uno con tarjeta de $200.000 | El cajero genera el cierre del 12/11 | Efectivo $150.000, tarjeta $200.000, transferencia $0, 3 facturas |
| AC-012.2 (borde) | El 15/11 no hubo pagos | El cajero genera el cierre del 15/11 | Todos los totales en $0 y 0 facturas |
| AC-013.1 | La 101 está libre del 20 al 22/11 | El agente crea una reserva por TELÉFONO | La reserva queda Confirmada con canal TELÉFONO y agente `agente` |
| AC-013.2 | La 101 tiene una reserva del 20 al 22/11 | El agente pide la 101 del 21 al 23/11 | Se rechaza por fechas cruzadas |
| AC-014.1 | Sesión de `agente` | Abre el detalle de una reserva confirmada | No aparecen check-in, check-out ni pagos |
| AC-015.1 | La 203 está en LIMPIEZA | El supervisor la asigna a la camarera `ana` | La 203 aparece en la lista de `ana` |
| AC-015.2 (borde) | La 203 está asignada a `ana` | El supervisor la reasigna a `luz` | La 203 sale de la lista de `ana` y aparece en la de `luz` |
| AC-016.1 | La 101 está OCUPADA | El supervisor intenta asignarla | Se rechaza: solo se asignan habitaciones en LIMPIEZA |
| AC-017.1 | `ana` tiene asignadas la 203 y la 204; la 205 está en LIMPIEZA sin asignar | `ana` abre su inicio | Ve la 203 y la 204, no la 205; el supervisor sí ve la 205 |

### R2-Sprint 3

| ID | Dado (Given) | Cuando (When) | Entonces (Then) |
|----|--------------|---------------|-----------------|
| AC-018.1 | 10 habitaciones y 6 ocupadas el 19/11 | El gerente abre su inicio el 19/11 | Ve ocupación del 60 %, la tarifa promedio y los ingresos del mes por categoría |
| AC-019.1 | Sesión de `gerente` | Recorre su inicio | No hay botones para crear, editar ni cobrar |
| AC-020.1 | Tipo Doble con tarifa base de $200.000 | El administrador crea una temporada del 15/12 al 15/01 a $300.000 | La temporada queda PENDIENTE y una noche del 20/12 sigue costando $200.000 |
| AC-021.1 | Esa temporada está PENDIENTE | El gerente la aprueba | Una noche del 20/12 cuesta $300.000 |
| AC-021.2 | Esa temporada está PENDIENTE | El gerente la rechaza | Queda RECHAZADA y la noche sigue en $200.000 |
| AC-022.1 | Envío de correo activo y huésped con correo | Se confirma su reserva | Recibe un correo con el código de la reserva |
| AC-023.1 | El servicio de correo no responde | Se confirma una reserva | La reserva queda creada y el fallo queda registrado |

## 10. Decisiones de clarificación (acta, D09)

Las preguntas salen de la spec v0.1. Las decisiones las toma el PO; las marcadas con (*) se confirman con el
docente, como gerencia del hotel, en la Review del R2-Sprint 1 (martes 10/11).

| # | Pregunta | Decisión | Quién decidió | Dónde queda |
|---|----------|----------|---------------|-------------|
| 1 | Al terminar la reparación, ¿la habitación queda DISPONIBLE o va a LIMPIEZA? | A LIMPIEZA: se limpia antes de ofrecerse | Jaider (PO) (*) | RN-04, REQ-HN-007 |
| 2 | ¿El administrador puede entrar a las pantallas de los roles nuevos? | No: la matriz de permisos no le da esas funciones. Conserva el acceso que tenía en el Release 1 a recepción, servicios y limpieza | Jaider (PO) | REQ-HN-004, AC-004.3 |
| 3 | En Render ya hay usuarios creados, ¿cómo aparecen los de prueba nuevos? | Se crean al arrancar solo los que falten; no se toca ningún usuario existente | Jaider (PO) | REQ-HN-003, AC-003.1 a 003.3 |
| 4 | Los roles del Sprint 2 y 3, ¿deben poder entrar desde el Sprint 1? | Sí: entran a su inicio, que dice qué funciones llegan y en qué sprint (lo pide el Sprint Goal del R2-Sprint 1) | Jaider (PO) | REQ-HN-002, AC-002.1 |
| 5 | ¿La recepcionista sigue registrando pagos mientras no existe el cajero? | Sí, hasta el R2-Sprint 2 (SER-11). Después solo el cajero | Jaider (PO) (*) | RN-05, REQ-HN-011 |
| 6 | Si una habitación tiene varios daños, ¿cuál ve el técnico? | El más reciente; el historial completo sigue en el reporte de limpieza (HAB-14) | Jaider (PO) | REQ-HN-006, AC-006.2 |

Lo que no se resolvió queda fuera del alcance (sección 3).

## 11. Historial de versiones

| Versión | Cuándo | Qué cambia | Tarjeta |
|---------|--------|------------|---------|
| 0.1 | 07/11/2026 | Contexto, actores, alcance, no-objetivos, glosario, historias y reglas; preguntas abiertas | D08 |
| 0.2 | 07/11/2026 | Decisiones de la clarificación (sección 10); sin marcadores [NECESITA ACLARACIÓN] | D09 |
| 1.0 | 07/11/2026 | REQ-HN-001 a 023, NFR-HN-001 a 005 y criterios AC; queda para la Puerta 1 | D10 |

Las tres versiones se hicieron el sábado 07/11 y no del miércoles 4 al viernes 6, como estaba planeado (ver IMP-02).

## Acta de la Puerta 1

| Revisión | Cumple |
|----------|:------:|
| Todo requisito tiene ID y al menos un criterio Given/When/Then | ☑ |
| Los bordes están cubiertos (AC-003.2, 004.3, 006.2, 008.2, 012.2, 015.2) | ☑ |
| No quedan marcadores [NECESITA ACLARACIÓN] | ☑ |
| Los requisitos no funcionales son medibles | ☑ |
| No aparece tecnología en la spec | ☑ |
| Cada regla de negocio indica su origen | ☑ |
| Las decisiones de la clarificación están escritas donde aparece cada concepto | ☑ |

**Resultado:** ☑ Aprobada ☐ Aprobada con observaciones ☐ Rechazada
**Aprueba (PO):** Jaider Javier Jaimes · **Fecha:** 07/11/2026
