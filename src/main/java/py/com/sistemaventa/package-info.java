/**
 * Sistema de Venta - paquete raiz.
 *
 * <p>Arquitectura por capas definida en {@code PROJECT_SPEC.md}:</p>
 *
 * <pre>
 * presentation -&gt; application -&gt; domain &lt;- repository &lt;- persistence
 * </pre>
 *
 * <p>Paquetes:</p>
 * <ul>
 *   <li>{@code bootstrap} - composition root y punto de entrada.</li>
 *   <li>{@code presentation} - JavaFX, FXML, CSS y controllers. Sin SQL, sin JDBC,
 *       sin acceso directo a repositorios y sin reglas de negocio.</li>
 *   <li>{@code application} - casos de uso, coordinacion, permisos y transacciones.</li>
 *   <li>{@code domain} - entidades, objetos de valor, estados, reglas y calculos puros.
 *       No depende de JavaFX, JDBC ni persistence.</li>
 *   <li>{@code repository} - interfaces orientadas al dominio.</li>
 *   <li>{@code persistence} - JDBC, SQL, mapeadores y repositorios concretos.</li>
 * </ul>
 *
 * <p>Componentes transversales:</p>
 * <ul>
 *   <li>{@code config} - carga de configuracion.</li>
 *   <li>{@code logging} - configuracion de SLF4J + Logback.</li>
 *   <li>{@code security} - autenticacion, RBAC y autorizacion.</li>
 *   <li>{@code validation} - validacion de entrada reutilizable.</li>
 *   <li>{@code transaction} - unidad de trabajo y demarcacion transaccional.</li>
 *   <li>{@code audit} - auditoria inmutable.</li>
 *   <li>{@code reporting} - generacion de reportes, PDF y Excel.</li>
 * </ul>
 */
package py.com.sistemaventa;
