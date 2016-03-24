package de.ars.daojones.drivers.notes.search;

import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.query.FieldComparison;

/**
 * An encoder converts a literal to a query language string.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the type of literal
 */
public interface Encoder<T> extends SearchTypeAware<T> {

  /**
   * Returns the type of the literal.
   * 
   * @return the type of the literal
   */
  @Override
  Class<? extends T> getKeyType();

  /**
   * Encodes the <tt>null</tt> literal.
   * 
   * @param context
   *          the encoder context
   * @return the <tt>null</tt> literal
   * @throws QueryLanguageException
   */
  String encodeNull( EncoderContext<T> context ) throws QueryLanguageException;

  /**
   * Encodes the literal. The literal must not be <tt>null</tt>. In case of null
   * values, {@link #encodeNull()} is invoked.
   * 
   * @param context
   *          the encoder context
   * @param provider
   *          the encoder provider
   * @param value
   *          the value
   * @return the literal
   * @see #encodeNull()
   * @throws NullPointerException
   *           if the value is <tt>null</tt>
   * @throws QueryLanguageException
   */
  String encodeLiteral( EncoderContext<T> context, EncoderProvider provider, final T value )
          throws QueryLanguageException;

  /**
   * The context of an encoder operation.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <T>
   *          the tpy ethat is encoded
   */
  interface EncoderContext<T> {

    /**
     * Returns the search criterion.
     * 
     * @return the search criterion
     */
    FieldComparison<T> getCriterion();

    /**
     * Returns the bean model.
     * 
     * @return the bean model
     */
    Bean getModel();

    /**
     * Returns the search type.
     * 
     * @return the search type
     */
    SearchType getSearchType();
  }

}
