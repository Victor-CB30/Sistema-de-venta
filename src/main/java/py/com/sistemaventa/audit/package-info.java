/**
 * Componente transversal {@code audit} (reservado, Fase 7).
 *
 * <p>Auditoria inmutable de las operaciones del sistema.</p>
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>Los registros de auditoria no se actualizan ni se eliminan.</li>
 *   <li>Nunca se almacenan secretos: ni contrasenas, ni tokens, ni cadenas de
 *       conexion.</li>
 *   <li>La escritura de auditoria se solicita desde {@code application}.</li>
 * </ul>
 */
package py.com.sistemaventa.audit;
