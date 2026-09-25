# PROJECT_SPEC — Sistema de Gestión Comercial

## Estado del documento

**Fase 1 — Análisis del sistema: CERRADA Y APROBADA**

Este archivo contiene las decisiones técnicas y funcionales que deben considerarse como referencia obligatoria durante el desarrollo. No deben modificarse en fases posteriores salvo que exista un impedimento técnico real documentado.

---

## 1. Objetivo general

Desarrollar un sistema de gestión comercial de escritorio para Windows orientado a pequeñas y medianas empresas de Paraguay, capaz de administrar ventas, inventario, clientes, proveedores, compras, cuentas por cobrar, cuotas, pedidos, delivery, caja, gastos, auditoría, reportes y respaldos.

El principio rector es la **trazabilidad**: ningún cambio de stock, dinero o deuda debe producirse sin un documento origen y sin un movimiento auditable.

---

## 2. Tecnologías congeladas

- Java 21 LTS.
- JavaFX 21 con FXML y CSS.
- Maven.
- MySQL 8.4 LTS.
- JDBC como tecnología única de persistencia.
- HikariCP para pool de conexiones.
- Flyway para migraciones.
- BCrypt para contraseñas.
- SLF4J + Logback para logging.
- JasperReports para reportes/PDF.
- Apache POI para exportación Excel.
- ESC/POS para impresión térmica.
- jpackage para instalador de Windows.

---

## 3. Arquitectura

Dirección principal:

`presentation -> application -> domain <- repository <- persistence`

### Presentation
JavaFX, FXML, CSS y Controllers. No contiene SQL ni reglas de negocio complejas.

### Application
Casos de uso, permisos, transacciones, coordinación de repositorios, auditoría y DTOs.

### Domain
Entidades, objetos de valor, estados, reglas de negocio y cálculos puros.

### Repository
Interfaces orientadas al dominio.

### Persistence
JDBC, SQL, mapeadores, HikariCP y repositorios concretos.

### Componentes transversales
- security
- validation
- transaction
- audit
- reporting
- config
- logging

### Dependencias prohibidas
- Controller -> JDBC/SQL.
- Controller -> Repository directo.
- Controller -> lógica de negocio compleja.
- Domain -> JavaFX/JDBC/Application.
- Persistence -> decisiones de negocio.
- Dependencias circulares.

---

## 4. Dinero y cantidades

### Dinero
- Java: `BigDecimal`, encapsulado en `Guaranies`.
- SQL: `DECIMAL(15,0)` para importes.
- Costos: `DECIMAL(15,4)`.
- Porcentajes: `DECIMAL(5,2)`.
- Moneda: PYG.
- Redondeo: `HALF_UP`.
- Presentación: `Gs. 150.000`.
- Prohibido usar `float` o `double` para dinero.

Las divisiones de cuotas o prorrateos deben conservar el total exacto. El resto se asigna a la última parte.

### Cantidades
- Java: `BigDecimal`, encapsulado en `Cantidad`.
- SQL: `DECIMAL(12,3)`.
- Escala máxima: 3 decimales.
- La unidad de medida define si acepta decimales y su escala.
- Una cantidad con más decimales de los permitidos se rechaza.

---

## 5. Seguridad

- RBAC configurable.
- Usuarios pueden tener múltiples roles.
- Permisos verificados en Application/Services.
- Autorización puntual de supervisor.
- Contraseñas BCrypt, costo 12.
- Bloqueo temporal por intentos fallidos.
- Timeout por inactividad.
- Credenciales de BD fuera del código.
- Usuario de aplicación con privilegios mínimos.
- Usuario separado para migraciones.
- Auditoría inmutable y sin secretos.
- PreparedStatement obligatorio.

---

## 6. Inventario

Se manejan:

- stock físico;
- stock reservado;
- stock disponible calculado.

`stockDisponible = stockFisico - stockReservado`

Los movimientos de inventario constituyen la evidencia histórica. Los acumulados de Producto son cachés operativos y deben actualizarse dentro de la misma transacción.

Toda operación que modifique stock debe:

1. iniciar transacción;
2. bloquear el producto;
3. revalidar stock;
4. modificar;
5. registrar movimiento;
6. confirmar o hacer rollback.

### Costos
- Último costo: última compra confirmada.
- Costo promedio ponderado: valoración del inventario y costo de salida.
- Costo histórico de venta: costo promedio congelado en DetalleVenta.

Las devoluciones reingresan con el costo histórico de la venta original.

---

## 7. Pedido, reserva y venta

Pedido y Venta son conceptos diferentes.

### Pedido
Puede ser modificado/cancelado según su estado. Al confirmarse crea reservas.

### Reserva
- Tiene expiración.
- No modifica stock físico.
- Incrementa stock reservado.
- Reduce stock disponible.

### Venta
Es una operación comercial confirmada e histórica.

Conversión:

`Pedido -> RETIRADO / ENTREGADO -> Venta`

En la conversión:
- se valida la reserva;
- se libera la reserva;
- se descuenta stock físico;
- se genera MovimientoInventario;
- se crea Venta;
- se registra Cobro o CuentaCobrar;
- se genera Comprobante.

---

## 8. Cobros

Se separan tres conceptos:

### Cobro
Operación económica de recepción de dinero.

### CobroMedioPago
Indica cómo se recibió:
- efectivo;
- transferencia;
- tarjeta;
- QR.

### ImputacionCobro
Indica qué obligación fue pagada:
- Venta;
- Cuota;
- Pedido (anticipo).

Un Cobro puede tener múltiples medios y múltiples imputaciones.

---

## 9. Anticipos

El anticipo no posee entidad independiente.

Se registra como:
- Cobro;
- ImputacionCobro al Pedido.

Cuando Pedido se convierte en Venta:
- la imputación original pasa a TRASLADADA;
- se crea una nueva imputación sobre la Venta;
- ambas usan el mismo Cobro;
- no se registra un nuevo ingreso de caja.

Si se cancela el Pedido, se genera un Reintegro.

No existe saldo a favor en el MVP.

---

## 10. Crédito y cuotas

MVP sin:
- intereses;
- recargos monetarios;
- refinanciación.

Incluye:
- entrega inicial;
- saldo financiado;
- cuotas;
- vencimientos;
- pagos parciales;
- pagos adelantados;
- cobro de varias cuotas;
- mora informativa;
- estado de cuenta.

Imputación normal: FIFO por vencimiento.

Una devolución reduce las últimas cuotas pendientes hacia las anteriores, preservando las cuotas ya pagadas.

---

## 11. Delivery y rendición

El costo de delivery es un cargo comercial y **no un producto**.

Los estados de delivery forman parte del Pedido.

### Efectivo contra entrega
La custodia se registra en `CobroMedioPago`.

Flujo:

`PENDIENTE_RENDICION -> DetalleRendicionRepartidor -> RendicionRepartidor -> MovimientoCaja -> RENDIDO`

### Transferencia/QR/tarjeta
No requieren rendición física. Utilizan referencia y verificación.

Una RendicionRepartidor puede contener múltiples DetalleRendicionRepartidor, cada uno asociado a un CobroMedioPago en efectivo.

---

## 12. Caja

Se utilizan:
- Terminal.
- SesionCaja.
- MovimientoCaja.
- CierreCajaDetalle.
- OperacionCaja.

Solo los movimientos en efectivo modifican el efectivo esperado del cajón.

Transferencias, QR y tarjetas se registran para conciliación pero no modifican el efectivo físico.

---

## 13. Documentos y trazabilidad

Existe una entidad base `Documento`.

Documentos derivados:
- Pedido.
- Venta.
- Compra.
- Cobro.
- Reintegro.
- Devolucion.
- AjusteInventario.
- Gasto.
- OperacionCaja.
- RendicionRepartidor.

MovimientoInventario, MovimientoCaja y Comprobante referencian Documento.

Los documentos confirmados son inmutables.

Correcciones posteriores se realizan mediante:
- anulación;
- devolución;
- reintegro;
- operación correctiva.

---

## 14. Comprobantes

MVP:
- ticket de venta;
- recibo de cobro;
- nota de devolución;
- comprobante de reintegro;
- comprobante de rendición;
- nota de pedido.

Son comprobantes internos.

SIFEN y facturación electrónica quedan fuera del MVP.

---

## 15. Backups

- respaldo diario;
- cifrado;
- suma de verificación;
- rotación;
- copia externa;
- prueba periódica de restauración.

---

## 16. Alcance del MVP

Incluye:

- autenticación;
- usuarios;
- roles y permisos;
- autorización de supervisor;
- datos del negocio;
- parámetros;
- terminales;
- categorías;
- marcas;
- unidades;
- proveedores;
- clientes;
- productos;
- historial de precios;
- inventario;
- kardex;
- ajustes;
- stock inicial;
- toma física;
- compras contado o crédito externo no controlado;
- caja;
- gastos;
- POS;
- ventas;
- pago mixto;
- crédito;
- cuotas;
- cuentas por cobrar;
- cobros;
- pedidos;
- reservas;
- retiro;
- delivery;
- anticipos;
- rendiciones;
- anulaciones;
- devoluciones;
- reintegros;
- comprobantes internos;
- ticket térmico;
- auditoría;
- dashboard;
- alertas;
- reportes;
- PDF;
- Excel;
- backups;
- verificación de consistencia;
- instalador Windows.

---

## 17. Fuera del MVP

- SIFEN/facturación electrónica.
- Intereses.
- Recargos monetarios por mora.
- Refinanciación.
- Saldo a favor.
- Cuentas por pagar completas.
- ProductoProveedor.
- Multisucursal.
- Múltiples depósitos.
- Depósito de repartidor.
- Tienda online.
- App móvil.
- Promociones avanzadas.
- Combos avanzados.
- Listas de precios.
- Fidelización.
- Lotes.
- vencimientos.
- números de serie.
- varias direcciones por cliente.
- recordatorios WhatsApp.
- etiquetas con código de barras.
- multimoneda.
- cuenta de faltantes del repartidor.
- arqueo por denominación.
- conciliación bancaria automática.

---

## 18. Políticas de negocio aprobadas

- Reserva para retiro: 48 horas.
- Reserva para delivery: 24 horas.
- Vencimiento de reserva: alerta y liberación manual.
- Crédito: habilitación individual por cliente con límite propio.
- Cliente con cuotas vencidas: requiere autorización.
- Entrega inicial mínima: 20 %, configurable.
- Máximo de cuotas: 12, configurable.
- Descuento cajero: 5 %.
- Descuento supervisor: 15 %.
- Administrador: puede superar límites con auditoría y validaciones.
- Venta bajo costo: permitida únicamente con autorización y motivo.
- Tolerancia de diferencia de caja: Gs. 5.000, configurable.
- Tolerancia de rendición: Gs. 5.000, configurable.
- Anticipos: obligatorios a partir de un umbral configurable; reembolso total cuando corresponda.
- Delivery fallido: hasta 2 reintentos sin nuevo cargo automático.
- Anulación de venta: dentro de la misma sesión de caja.
- Devolución: hasta 7 días con comprobante, configurable.

---

## 19. Regla para agentes de código

Antes de modificar el proyecto:

1. Leer este archivo.
2. Inspeccionar el repositorio.
3. Respetar tecnologías, arquitectura y alcance congelados.
4. No agregar módulos fuera del MVP.
5. No modificar decisiones congeladas sin documentar un impedimento técnico.
6. Trabajar únicamente sobre la fase actual.
7. Ejecutar compilación/pruebas al terminar.
8. Documentar archivos creados, modificados, pruebas y resultado.
