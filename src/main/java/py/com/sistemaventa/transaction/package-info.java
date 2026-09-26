/**
 * Componente transversal {@code transaction} (reservado, Fase 4).
 *
 * <p>Unidad de trabajo y demarcacion transaccional. Define el ambito de una
 * operacion critica, su confirmacion y su rollback.</p>
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>Los limites transaccionales se decisen en {@code application}, nunca en
 *       {@code presentation}.</li>
 *   <li>Toda operacion que modifique stock o dinero se ejecuta completa o no se
 *       ejecuta.</li>
 *   <li>La implementacion puede vivir en {@code persistence}; este paquete solo
 *       define el contrato.</li>
 * </ul>
 */
package py.com.sistemaventa.transaction;
