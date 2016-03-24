package de.ars.daojones.drivers.notes.search;

import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterion;

/**
 * An implementation of this interface is used to build the query string to
 * search within the database.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the type of the search criterion
 */
public interface QueryLanguageBuilder<T extends SearchCriterion> extends SearchTypeAware<T> {

  /**
   * Returns the type of the search criterion that this builder is responsible
   * for. This can also be a super type for multiple criteria.
   * 
   * @return the type of the search criterion that this builder is responsible
   *         for
   */
  @Override
  Class<? extends T> getKeyType();

  /**
   * Creates the query for a given search criterion and a bean model.
   * 
   * @param buffer
   *          the buffer where the query string is appended
   * @param context
   *          the query context
   * @throws QueryLanguageException
   *           if creating the query string occurs an error
   */
  void createQuery( StringBuilder buffer, QueryContext<T> context ) throws QueryLanguageException;

  /**
   * A context object for an invocation of query creation.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <T>
   *          the type of the search criterion
   */
  interface QueryContext<T extends SearchCriterion> {
    /**
     * Returns the query language that can be used for delegation. This is
     * necessary when the search criterion contains further criteria that have
     * to be requested for query creation too.
     * 
     * @return the query language
     */
    QueryLanguage getLanguage();

    /**
     * Returns the search criterion that currently has to be transformed to a
     * query string.
     * 
     * @return the search criterion
     */
    T getCriterion();

    /**
     * Returns the bean model.
     * 
     * @return the bean model
     */
    Bean getModel();

    /**
     * Returns the query object.
     * 
     * @return the query object
     */
    Query getQuery();

    /**
     * Returns the type of search.
     * 
     * @return the type of search
     */
    SearchType getSearchType();

  }

}
