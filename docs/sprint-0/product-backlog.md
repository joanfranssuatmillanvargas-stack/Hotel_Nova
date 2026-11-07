# Product Goal y Product Backlog inicial de HotelNova

**Tarjeta:** D03 · **Responsable:** Jaider Javier Jaimes (PO) · **Revisa:** Joan Franssuat Millán Vargas
**Versión:** 1.0 · Sprint 0 · La misma tabla está en la hoja **Backlog_PBI** del Excel.

## Product Goal

> "Que el hotel opere su día completo en HotelNova, desde la reserva por teléfono hasta el cierre de
> caja, con 10 perfiles de usuario que solo ven lo que les corresponde, sin reservas cruzadas, sin
> habitaciones dañadas ofrecidas a huéspedes y sin consumos sin cobrar."

## Punto de partida

Este backlog es el del **Release 2** del mismo producto; sus sprints se llaman R2-Sprint 0 a 3.

HotelNova ya tiene construidos los 4 microservicios, hotel-web y 5 roles, desplegados en Render. Este
backlog no repite ese trabajo. Agrega lo necesario para pasar a 10 roles bajo el método Scrum + SDD,
y la línea base se especifica formalmente en la spec v1.0 (PBI-01).

## Product Backlog

| ID | Ítem | Tipo | MoSCoW | Pts | Sprint | Criterios gruesos de aceptación |
|----|------|------|--------|:---:|:------:|---------------------------------|
| PBI-01 | Spec v1.0, plan y tareas de HotelNova con 10 roles | Habilitador | Must | 5 | S1 | 1) spec.md v1.0 aprobada en la Puerta 1, con los 10 roles y sus reglas. 2) plan.md, ADR y openapi.yaml aprobados en la Puerta 2; tasks.md aprobado en la Puerta 3. |
| PBI-02 | Base de seguridad para 10 roles | Habilitador | Must | 5 | S1 | 1) Existen los 10 roles con un usuario de prueba cada uno y cada uno entra a su propio inicio. 2) **Borde:** un rol que abre la ruta de otro es devuelto a su inicio sin ver datos. |
| PBI-03 | HU · Técnico de mantenimiento repara y libera habitaciones | Historia | Must | 3 | S1 | 1) El técnico ve las habitaciones en MANTENIMIENTO con su daño; al registrar la reparación pasan a LIMPIEZA. 2) **Error:** no se puede registrar la reparación de una habitación que no está en MANTENIMIENTO. |
| PBI-04 | HU · Cajero cobra facturas y cierra caja | Historia | Must | 5 | S2 | 1) El cajero registra el pago de una factura PENDIENTE y obtiene el cierre del día por medio de pago. 2) **Error:** una factura ya pagada no se cobra dos veces; la recepcionista ya no puede registrar pagos. |
| PBI-05 | HU · Agente de reservas toma reservas por teléfono o correo | Historia | Must | 5 | S2 | 1) El agente crea una reserva para un huésped (nuevo o existente) indicando el canal. 2) **Borde:** no puede hacer check-in ni check-out, y la regla de fechas cruzadas se sigue aplicando. |
| PBI-06 | HU · Supervisor de limpieza asigna habitaciones a camareras | Historia | Should | 3 | S2 | 1) El supervisor asigna cada habitación en LIMPIEZA a una camarera, y ella solo ve las suyas. 2) **Borde:** una habitación sin asignar sigue visible para el supervisor, y al reasignarla desaparece de la lista de la camarera anterior. |
| PBI-07 | HU · Gerente consulta el tablero gerencial y aprueba temporadas | Historia | Should | 5 | S3 | 1) El gerente ve ocupación, tarifa promedio e ingresos por periodo. 2) **Borde:** una temporada nueva queda PENDIENTE y no cambia el precio hasta que el gerente la aprueba. |
| PBI-08 | Calidad: trazabilidad completa y despliegue de los 10 roles | Calidad | Should | 3 | S3 | 1) Todos los requisitos tienen prueba en la matriz de trazabilidad. 2) Los 10 roles funcionan en Render con sus usuarios de prueba. |
| PBI-09 | HU · Confirmación de reserva por correo al huésped | Historia | Could | 3 | Reserva | 1) Al confirmar una reserva el huésped recibe un correo con el código. 2) **Error:** si el correo falla, la reserva queda creada igual y el fallo se registra. |

**Total inicial:** 37 puntos (34 comprometidos en los sprints + 3 de reserva).

- **Sprint 1:** 13 puntos (PBI-01, PBI-02, PBI-03).
- **Sprint 2:** 13 puntos (PBI-04, PBI-05, PBI-06).
- **Sprint 3:** 8 puntos (PBI-07, PBI-08) + PBI-09 como objetivo extendido.

Si en la Review del Sprint 2 aparece una solicitud de cambio, entra al backlog como **PBI-10 (CR-001)**.
La decisión es del PO y se refina con un delta de la spec.

## Sprint Goals

| Sprint | Fechas | Sprint Goal | Ítems |
|--------|--------|-------------|-------|
| R2-0 | 28/10 – 03/11/2026 | El equipo puede empezar: roles definidos, backlog ordenado, tableros montados y constitución aprobada | Preparación (D01 a D06) |
| R2-1 | 04/11 – 10/11/2026 | Al terminar el sprint, la spec v1.0 está aprobada con su plan y tareas, los 10 roles pueden iniciar sesión cada uno en su inicio, y un técnico puede reparar una habitación dañada y devolverla al servicio | PBI-01, PBI-02, PBI-03 (13 pts) |
| R2-2 | 11/11 – 17/11/2026 | Al terminar el sprint, un agente puede tomar reservas por teléfono, el cajero cobra y cierra la caja del día, y el supervisor reparte las habitaciones entre las camareras | PBI-04, PBI-05, PBI-06 (13 pts) |
| R2-3 | 18/11 – 24/11/2026 | Al terminar el sprint, el gerente ve el tablero gerencial y aprueba temporadas, los 10 roles están desplegados con trazabilidad completa, y el PO toma la decisión de release | PBI-07, PBI-08 (8 pts); PBI-09 extendido |

**Nota:** el lunes 16/11/2026 es festivo (Independencia de Cartagena) y cae en el día 4 del Sprint 2.
Por eso el Sprint 2 tiene un día hábil menos.

## Fichas de los ítems

### PBI-01 · Spec v1.0, plan y tareas (Habilitador · Must · 5 pts · S1)
- **Intención:** como equipo, queremos la especificación aprobada de HotelNova con sus 10 roles, para que la IA y nosotros construyamos solo lo que se pidió.
- **Contiene:** spec v0.1 → v0.2 (clarificación con el docente como gerencia) → v1.0 (EARS y Given/When/Then), plan.md con ADR, openapi.yaml y tasks.md.

### PBI-02 · Base de seguridad para 10 roles (Habilitador · Must · 5 pts · S1)
- **Intención:** como administrador quiero que cada empleado entre con su propio rol, para que nadie vea ni haga lo que no le corresponde.
- **Contiene:** los 5 roles nuevos en el enum de roles, los permisos por ruta, los usuarios de prueba y el menú de cada rol.

### PBI-03 · Técnico de mantenimiento (Historia · Must · 3 pts · S1)
- **Intención:** como técnico de mantenimiento quiero ver las habitaciones dañadas y registrar su reparación, para que vuelvan al servicio limpias y sin demora.

### PBI-04 · Cajero (Historia · Must · 5 pts · S2)
- **Intención:** como cajero quiero cobrar las facturas y cerrar la caja del día, para que el dinero cuadre con lo facturado.

### PBI-05 · Agente de reservas (Historia · Must · 5 pts · S2)
- **Intención:** como agente de reservas quiero crear reservas por teléfono o correo indicando el canal, para no depender de la recepción y saber de dónde llegan los clientes.

### PBI-06 · Supervisor de limpieza (Historia · Should · 3 pts · S2)
- **Intención:** como supervisor de limpieza quiero asignar las habitaciones a cada camarera, para repartir el trabajo y saber quién limpió qué.

### PBI-07 · Gerente (Historia · Should · 5 pts · S3)
- **Intención:** como gerente quiero ver los indicadores del hotel y aprobar las tarifas de temporada, para decidir con datos sin tocar la operación.

### PBI-08 · Calidad y despliegue (Calidad · Should · 3 pts · S3)
- **Intención:** como PO quiero la matriz de trazabilidad completa y los 10 roles en Render, para tomar la decisión de release con evidencia.

### PBI-09 · Confirmación por correo (Historia · Could · 3 pts · Reserva)
- **Intención:** como huésped quiero recibir un correo con el código de mi reserva, para tener constancia.
- **Nota:** objetivo extendido; no se inicia salvo que sobre capacidad en el Sprint 3.
