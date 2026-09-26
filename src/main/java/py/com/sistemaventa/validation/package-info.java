/**
 * Componente transversal {@code validation} (reservado).
 *
 * <p>Validaciones de entrada reutilizables y de soporte a la capa
 * {@code application}: formato, obligatoriedad, rangos y reglas de formulario.</p>
 *
 * <p>Reglas:</p>
 * <ul>
 *   <li>No reemplaza las invariantes del dominio: esas viven en {@code domain}.</li>
 *   <li>Devuelve informacion del error apta para mostrar en
 *       {@code presentation}.</li>
 * </ul>
 */
package py.com.sistemaventa.validation;
