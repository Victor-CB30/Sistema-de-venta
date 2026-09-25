/**
 * Capa {@code persistence} (reservada, Fase 4).
 *
 * <p>JDBC, SQL, mapeadores, HikariCP y repositorios concretos.</p>
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>Implementa las interfaces de {@code repository}.</li>
 *   <li>{@code PreparedStatement} obligatorio; nunca concatenacion de SQL.</li>
 *   <li>No contiene decisiones de negocio: solo traduce entre el modelo relacional
 *       y {@code domain}.</li>
 *   <li>No es accesible desde {@code presentation} ni desde {@code application}.</li>
 * </ul>
 */
package py.com.sistemaventa.persistence;
