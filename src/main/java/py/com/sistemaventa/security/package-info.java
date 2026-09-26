/**
 * Componente transversal {@code security} (reservado, Fase 6).
 *
 * <p>Responsabilidades previstas:</p>
 * <ul>
 *   <li>autenticacion de usuarios;</li>
 *   <li>RBAC con roles multiples por usuario;</li>
 *   <li>verificacion de permisos en {@code application};</li>
 *   <li>autorizacion puntual de supervisor;</li>
 *   <li>BCrypt con costo 12;</li>
 *   <li>bloqueo temporal por intentos fallidos y timeout por inactividad.</li>
 * </ul>
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>Nunca se registran contrasenas ni hashes en logs.</li>
 *   <li>Las credenciales de base de datos no viven en el codigo.</li>
 *   <li>La verificacion de permisos ocurre en {@code application}, no en
 *       {@code presentation}.</li>
 * </ul>
 */
package py.com.sistemaventa.security;
