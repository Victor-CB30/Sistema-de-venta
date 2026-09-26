/**
 * Componente transversal {@code reporting} (reservado, Fase 25).
 *
 * <p>Generacion de reportes y exportacion a PDF y Excel.</p>
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>Los reportes leen el modelo de dominio a traves de {@code application};
 *       no consultan la base de datos directamente.</li>
 *   <li>Los importes se formatean con el valor de {@code Guaranies}, nunca con
 *       {@code double}.</li>
 *   <li>No contiene reglas de negocio.</li>
 * </ul>
 */
package py.com.sistemaventa.reporting;
