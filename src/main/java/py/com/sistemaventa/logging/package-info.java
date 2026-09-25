/**
 * Componente transversal {@code logging}.
 *
 * <p>Configura SLF4J + Logback en tiempo de arranque: directorio de logs y
 * nivel raiz, inyectados antes de la primera obtencion de un logger.</p>
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>Nunca se registran contrasenas, tokens ni cadenas de conexion.</li>
 *   <li>Los logs se escriben en el directorio de datos del usuario, nunca junto
 *       al ejecutable.</li>
 * </ul>
 *
 * @see py.com.sistemaventa.logging.LoggingConfigurator
 */
package py.com.sistemaventa.logging;
