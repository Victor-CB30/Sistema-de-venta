# FASE 1 — Análisis del Sistema

**Estado:** Completada y aprobada.

## Objetivo

Definir el alcance funcional y técnico de un sistema completo de gestión comercial antes de iniciar la implementación.

## Principio central

La solución adopta trazabilidad completa:

- ningún cambio de stock sin documento y movimiento;
- ningún movimiento de dinero sin documento;
- ninguna modificación de deuda sin imputación o documento correctivo;
- documentos confirmados inmutables;
- operaciones críticas transaccionales.

## Módulos definidos

1. Autenticación.
2. Usuarios.
3. Roles y permisos.
4. Configuración.
5. Dashboard.
6. Categorías.
7. Marcas.
8. Unidades de medida.
9. Productos.
10. Inventario.
11. Proveedores.
12. Compras.
13. Clientes.
14. POS/Ventas.
15. Cobros.
16. Cuentas por cobrar.
17. Cuotas.
18. Pedidos.
19. Reservas.
20. Delivery.
21. Rendiciones.
22. Caja.
23. Gastos.
24. Devoluciones.
25. Anulaciones.
26. Comprobantes.
27. Auditoría.
28. Reportes.
29. Backups.

## Decisiones arquitectónicas

- Arquitectura por capas.
- JDBC como persistencia principal.
- Repositorios orientados al dominio.
- HikariCP.
- Flyway.
- MySQL 8.4 LTS.
- BigDecimal para dinero y cantidades.
- RBAC.
- Documento base común.
- Auditoría inmutable.

## Inventario

Se diferencia:

- stock físico;
- stock reservado;
- stock disponible calculado.

Los movimientos son la fuente histórica del stock.

Se exige control de concurrencia y revalidación dentro de la transacción.

## Ventas

Una Venta:
- se confirma una sola vez;
- contiene sus valores históricos;
- no se edita después de confirmada;
- puede anularse o tener devoluciones.

## Crédito

Incluye:
- entrega inicial;
- saldo;
- cuotas;
- pagos parciales;
- mora informativa.

No incluye intereses ni refinanciación en el MVP.

## Pedidos

Pedido y Venta son distintos.

Un pedido confirmado reserva stock.

La venta se crea al retirar o entregar.

## Cobros

Separación:

`Cobro -> CobroMedioPago`

`Cobro -> ImputacionCobro`

Esto permite pagos mixtos y aplicación a distintas obligaciones.

## Delivery

El dinero efectivo cobrado por repartidor queda en custodia hasta su rendición.

Los medios electrónicos no requieren rendición física.

## Caja

Caja por terminal y sesión.

Solo el efectivo modifica el efectivo esperado.

## Resultado

La Fase 1 produce:
- especificación funcional;
- decisiones técnicas;
- reglas de negocio;
- alcance MVP;
- roadmap;
- políticas del negocio.

Las decisiones consolidadas se encuentran en `PROJECT_SPEC.md`.
