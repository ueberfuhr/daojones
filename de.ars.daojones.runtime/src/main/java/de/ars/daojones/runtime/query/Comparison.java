package de.ars.daojones.runtime.query;

/**
 * A simple interface for a comparison operator. This is used by
 * {@link FieldComparison}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 * @param <T>
 *          the type of the values that should be compared
 */
public interface Comparison<T> {

  /**
   * Compares two values with each other.
   * 
   * @param left
   *          the left value
   * @param right
   *          the right value
   * @return <tt>true</tt>, if the comparison matches both values
   */
  boolean matches( T left, T right );

  /**
   * Returns the type that is compared.
   * 
   * @return the type
   */
  Class<T> getType();

}
