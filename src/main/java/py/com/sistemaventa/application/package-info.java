/**
 * Capa {@code application}.
 *
 * <p>Casos de uso, coordinacion de repositorios, permisos, transacciones y DTOs.</p>
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>No depende de {@code presentation} ni de {@code persistence}.</li>
 *   <li>No contiene SQL ni JDBC.</li>
 *   <li>Orquesta {@code domain} y {@code repository}; no decide reglas de negocio,
 *       que pertenecen a {@code domain}.</li>
 * </ul>
 */
package py.com.sistemaventa.application;
