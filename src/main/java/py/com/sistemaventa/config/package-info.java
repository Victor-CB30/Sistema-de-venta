/**
 * Componente transversal {@code config}.
 *
 * <p>Carga y expone la configuracion tecnica de la aplicacion
 * (archivo {@code application.properties} empaquetado como recurso).</p>
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>Ninguna credencial de base de datos se almacena en el codigo ni en el
 *       recurso empaquetado.</li>
 *   <li>Los valores se leen de forma tipada y con valores por defecto seguros.</li>
 *   <li>No se permiten dependencias hacia JavaFX, domain ni persistence.</li>
 * </ul>
 *
 * @see py.com.sistemaventa.config.AppConfig
 */
package py.com.sistemaventa.config;
