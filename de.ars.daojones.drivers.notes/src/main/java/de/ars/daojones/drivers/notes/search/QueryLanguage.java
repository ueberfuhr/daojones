package de.ars.daojones.drivers.notes.search;

import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.query.Query;

/**
 * The query language is responsible for creating query strings. This interface
 * is not intended to get implemented by extensions.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface QueryLanguage {

  /**
   * Creates the query string for a given search criterion and a bean model. It
   * is not intended to implement this interface by driver extensions.
   * 
   * @param sb
   *          the buffer where the query string is appended
   * @param query
   *          the query object
   * @param model
   *          the bean model
   * @throws QueryLanguageException
   *           if creating the query string occurs an error
   */
  void createQuery( StringBuilder sb, Query query, Bean model ) throws QueryLanguageException;

}