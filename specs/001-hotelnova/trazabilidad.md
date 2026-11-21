# Matriz de trazabilidad: requisito → criterio → prueba · HotelNova Release 2

**PBI:** PBI-08 · **Tarea:** T025 (PLA-11) · **Cubre:** NFR-HN-005 · **Redacta:** Jaider Javier Jaimes · **Revisa:** Joan Franssuat Millán Vargas
**Entra:** `spec.md` (REQ-HN, NFR-HN y AC), `tasks.md`, las pruebas de `src/test/java` y la hoja *8. Plan Pruebas* del Excel
**Estado al 21/11/2026** (P032 a P034 aprobadas en Render) · Se vuelve a revisar en la Review del 24/11 (NFR-HN-005)

Cómo se lee: cada requisito tiene sus criterios de aceptación (AC), la prueba unitaria que los verifica (clase y método,
en verde en `mvn test`) y la prueba manual del plan de pruebas (P0xx) que se hace sobre la aplicación desplegada.

## Resumen

| Concepto | Valor |
|----------|-------|
| Requisitos de la spec | 23 funcionales (REQ-HN-001 a 023) + 5 no funcionales (NFR-HN-001 a 005) = 28 |
| Comprometidos en el Release 2 | 26 (REQ-HN-022 y 023 son de RES-13, objetivo extendido que no entró) |
| Con prueba unitaria | 23 de 26 (REQ-HN-001 a 021 y NFR-HN-001 y 002); NFR-HN-003 a 005 se verifican a mano o por revisión |
| Con prueba manual aprobada | REQ-HN-001 a 021 y NFR-HN-002 a 004 (P021 a P034, en Render el 09/11, el 16/11 y el 21/11) |
| Pendientes de prueba manual | Ninguno |
| Pruebas unitarias | 96 de 96 en verde (hotel-web 61, ms-habitaciones 21, ms-reservas 7, ms-servicios-facturacion 7) |

## R2-Sprint 1 · PBI-02 y PBI-03 (roles, permisos y mantenimiento)

| Requisito | Historia | Criterios | Prueba unitaria | Prueba manual | Estado |
|-----------|----------|-----------|-----------------|---------------|--------|
| REQ-HN-001 · 10 roles | PLA-08 | AC-001.1, 001.2 | `UsuariosDePruebaTest.ac001_1_elSistemaTiene10RolesConSuPropioInicio` | P021 | Aprobado |
| REQ-HN-002 · inicio y menú del rol | PLA-08 | AC-002.1, 002.2 | `PermisosPorRolTest.ac002_1_cadaRolEntraASuPropioInicio` | P021 | Aprobado |
| REQ-HN-003 · usuarios de prueba | PLA-08 | AC-003.1, 003.2, 003.3 | `UsuariosDePruebaTest.ac003_1`, `ac003_2`, `ac003_3` | P021 | Aprobado |
| REQ-HN-004 · página de otro rol | PLA-09 | AC-004.1 a 004.4 | `PermisosPorRolTest.ac004_unaRutaDeOtroRolDevuelveAlInicioPropio` | P022 | Aprobado |
| REQ-HN-005 · sin sesión | PLA-09 | AC-005.1 | `PermisosPorRolTest.ac005_1_sinSesionSeVaAlLogin` | P022 | Aprobado |
| REQ-HN-006 · lista del técnico | HAB-16 | AC-006.1, 006.2, 006.3 | `MantenimientoTest.ac006_1`, `ac006_2`, `ac006_3` | P023 | Aprobado |
| REQ-HN-007 · registrar reparación | HAB-17 | AC-007.1, 007.2 | `MantenimientoTest.ac007_1`, `ac007_2` | P024 | Aprobado |
| REQ-HN-008 · solo en MANTENIMIENTO | HAB-17 | AC-008.1, 008.2 | `MantenimientoTest.ac008_1`, `ac008_2` | P024 | Aprobado |

## R2-Sprint 2 · PBI-04 a PBI-06 (cajero, agente y supervisor)

| Requisito | Historia | Criterios | Prueba unitaria | Prueba manual | Estado |
|-----------|----------|-----------|-----------------|---------------|--------|
| REQ-HN-009 · pago del cajero | SER-11 | AC-009.1 | `CajaTest.ac009_1`, `req009_sinCajeroNoSeRegistraElPago` | P025 | Aprobado |
| REQ-HN-010 · no cobrar dos veces | SER-11 | AC-010.1 | `CajaTest.ac010_1_unaFacturaPagadaNoSeCobraDosVeces` | P025 (y paso 9 de la demo) | Aprobado |
| REQ-HN-011 · solo el cajero cobra | SER-11 | AC-011.1 | `PermisosSprint2Test.unaPaginaDeOtroRolDevuelveAlInicioPropio` (recepción → /caja) | P026 | Aprobado |
| REQ-HN-012 · cierre de caja | SER-12 | AC-012.1, 012.2 | `CajaTest.ac012_1`, `ac012_2_unDiaSinPagosDaTodoEnCero` | P027 | Aprobado |
| REQ-HN-013 · canal y quién reserva | RES-11 | AC-013.1, 013.2 | `AgenteReservasTest.ac013_1`, `ac013_2`, `rn07_*` | P028 | Aprobado |
| REQ-HN-014 · agente sin check-in ni pagos | RES-12 | AC-014.1 | `PermisosSprint2Test.unaPaginaDeOtroRolDevuelveAlInicioPropio` (agente → check-in, check-out, estadía, caja) | P029 | Aprobado |
| REQ-HN-015 · asignar y reasignar | HAB-18 | AC-015.1, 015.2 | `AsignacionTest.ac015_1`, `ac015_2`, `req015_*`; `CamarerasTest.ac015_1` | P030, P031 | Aprobado |
| REQ-HN-016 · solo LIMPIEZA y camareras | HAB-18 | AC-016.1 | `AsignacionTest.ac016_1`; `CamarerasTest.req016_*` | P030 | Aprobado |
| REQ-HN-017 · cada camarera ve las suyas | HAB-19 | AC-017.1 | `AsignacionTest.ac017_1_cadaCamareraVeSoloLasSuyasYElSupervisorVeTodas` | P031 | Aprobado |

## R2-Sprint 3 · PBI-07 y PBI-08 (gerente, calidad y despliegue)

| Requisito | Historia | Criterios | Prueba unitaria | Prueba manual | Estado |
|-----------|----------|-----------|-----------------|---------------|--------|
| REQ-HN-018 · tablero gerencial | PLA-10 | AC-018.1 | `TableroGerenciaTest.ac018_1`, `consultaLosCuatroMicroserviciosConElPeriodoElegido`; `TarifaPromedioTest.ac018_1` | P032 | Aprobado (21/11) |
| REQ-HN-019 · gerente sin crear, editar ni cobrar | PLA-10 | AC-019.1 | `PermisosSprint3Test.unaPaginaDeOtroRolDevuelveAlInicioPropio` (gerente → admin, recepción, caja, agente) | P032 | Aprobado (21/11) |
| REQ-HN-020 · temporada nueva PENDIENTE | HAB-20 | AC-020.1 | `TemporadasTest.ac020_1`, `alEditarUnaTemporadaAprobadaVuelveAPendiente`, `laTemporadaDelRelease1SinEstadoSigueVigente` | P033 | Aprobado (21/11) |
| REQ-HN-021 · aprobar o rechazar | HAB-20 | AC-021.1, 021.2 | `TemporadasTest.ac021_1`, `ac021_2`, `soloSeDecidenTemporadasPendientes`, `sinGerenteOConDecisionInvalidaResponde400` | P033 | Aprobado (21/11) |
| REQ-HN-022 · correo con el código | RES-13 | AC-022.1 | — | — | Fuera del release (objetivo extendido no iniciado) |
| REQ-HN-023 · fallo del correo | RES-13 | AC-023.1 | — | P035 (no se ejecuta) | Fuera del release |

## No funcionales

| Requisito | Cómo se verifica | Prueba | Estado |
|-----------|------------------|--------|--------|
| NFR-HN-001 · contraseñas cifradas | `UsuariosDePruebaTest.nfr001_laContrasenaDePruebaNoQuedaEnTextoPlano` | Unitaria | Aprobado |
| NFR-HN-002 · cada rol ve solo lo suyo | `PermisosPorRolTest.nfr002_matrizCompletaDeLos10RolesContraLas10Secciones` y `PermisosSprint2Test`, `PermisosSprint3Test` | Unitaria + P022 | Aprobado |
| NFR-HN-003 · técnico en un celular de 360 px | Abrir `/mantenimiento` con `mantenimiento` en el celular, sin desplazamiento horizontal | Dentro de P034 | Aprobado (21/11) |
| NFR-HN-004 · 10 roles en la URL pública | Iniciar sesión en Render con los 10 usuarios de prueba | P034 | Aprobado (21/11) |
| NFR-HN-005 · todo requisito con prueba | Esta matriz | Revisión en la Review del 24/11 | En revisión |

## Pruebas manuales del R2-Sprint 3 (aprobadas el sábado 21/11, evidencias en `docs/scrum/sprint-3/evidencias/`)

| Prueba | Qué se hace en Render | Resultado esperado |
|--------|-----------------------|--------------------|
| P032 | `gerente` abre su inicio | Ve ocupación de hoy, tarifa promedio e ingresos del mes por categoría; no hay botones para crear, editar ni cobrar; `/admin/habitaciones` y `/caja` lo devuelven a Gerencia |
| P033 | `admin` crea una temporada para el tipo Doble que cubra una fecha futura; recepción consulta el valor de esa noche; `gerente` la aprueba y se vuelve a consultar | Queda PENDIENTE y la noche cuesta la tarifa base; después de aprobarla cuesta la tarifa de la temporada |
| P034 | Iniciar sesión con los 10 usuarios de prueba en la URL pública; con `mantenimiento`, abrir su inicio en el celular | Cada uno entra a su inicio y ve solo su menú; la pantalla del técnico no se desplaza a los lados |
