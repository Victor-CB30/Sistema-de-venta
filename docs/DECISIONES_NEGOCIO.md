# Decisiones de Negocio Aprobadas

## 1. Reservas

- Retiro: 48 horas.
- Delivery: 24 horas.
- Al vencerse: generar alerta.
- Liberación: manual en el MVP.

## 2. Crédito

- El crédito se habilita individualmente por cliente.
- Cada cliente posee límite propio.
- Si tiene cuotas vencidas, una nueva venta a crédito requiere autorización.

## 3. Entrega inicial

- Mínimo predeterminado: 20 %.
- Debe ser configurable.

## 4. Máximo de cuotas

- Predeterminado: 12.
- Debe ser configurable.

## 5. Descuentos

- Cajero: hasta 5 %.
- Supervisor: hasta 15 %.
- Administrador: puede superar los límites con auditoría y respetando validaciones de integridad.

## 6. Venta por debajo del costo

Permitida únicamente con:
- permiso;
- autorización;
- motivo obligatorio;
- auditoría.

## 7. Diferencias

- Caja: tolerancia predeterminada Gs. 5.000.
- Rendición: tolerancia predeterminada Gs. 5.000.
- Ambos valores son configurables.

## 8. Anticipos

- Se puede exigir anticipo cuando el pedido supera un umbral configurable.
- El importe/porcentaje exigido será configurable.
- Cuando corresponda cancelar y devolver, el reintegro es total.
- No existe saldo a favor en el MVP.

## 9. Delivery fallido

- Hasta 2 reintentos sin nuevo cargo automático.
- Casos adicionales requieren decisión manual.
- El cargo de delivery se cobra al concretar la entrega.

## 10. Anulaciones y devoluciones

- Anulación: dentro de la misma sesión de caja.
- Devolución: hasta 7 días con comprobante.
- El plazo de devolución debe ser configurable.
