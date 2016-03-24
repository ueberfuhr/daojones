package de.ars.daojones.drivers.notes.search;

import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder.QueryContext;
import de.ars.daojones.runtime.query.Comparison;
import de.ars.daojones.runtime.query.FieldComparison;

/**
 * An implementation of this interface is used to build the query string for a
 * field comparison.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <X>
 *          the type that is compared
 */
public interface ComparisonBuilder<X> extends SearchTypeAware<Comparison<X>> {

  /**
   * Returns the type of the comparison.
   * 
   * @return the type of the comparison
   */
  @Override
  Class<? extends Comparison<X>> getKeyType();

  /**
   * Returns the type of the field.
   * 
   * @return the type of the field
   */
  Class<? extends X> getFieldType();

  /**
   * Creates the query for a given field comparison.
   * 
   * @param buffer
   *          the buffer where the query string is appended
   * @param context
   *          the comparison context
   * @throws QueryLanguageException
   *           if creating the query string occurs an error
   */
  void createQuery( StringBuilder buffer, ComparisonContext<X> context ) throws QueryLanguageException;

  /**
   * A context object for an invocation of query creation.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <T>
   *          the type of the field
   */
  interface ComparisonContext<T> extends EncoderProvider {

    /**
     * Returns the query context.
     * 
     * @return the query context
     */
    QueryContext<FieldComparison<T>> getQueryContext();

  }

}
