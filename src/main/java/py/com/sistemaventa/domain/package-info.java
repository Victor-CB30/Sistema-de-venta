/**
 * Capa {@code domain} (reservada, Fase 5).
 *
 * <p>Entidades, objetos de valor, estados, reglas de negocio y calculos puros.</p>
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>No depende de JavaFX, JDBC, {@code persistence} ni {@code application}.</li>
 *   <li>Dinero en {@code BigDecimal} encapsulado en {@code Guaranies}.</li>
 *   <li>Cantidades en {@code BigDecimal} encapsuladas en {@code Cantidad},
 *       escala maxima 3.</li>
 *   <li>Redondeo {@code RoundingMode.HALF_UP}. Prohibido {@code float} y {@code double}
 *       para dinero.</li>
 * </ul>
 */
package py.com.sistemaventa.domain;
