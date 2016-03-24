package de.ars.daojones.drivers.notes.search;

import de.ars.daojones.internal.drivers.notes.search.QueryLanguageImpl;

/**
 * A factory that creates query language instances.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class QueryLanguageFactory {

  /**
   * Creates a query language instance.
   * 
   * @return the query language instance
   */
  public QueryLanguage createQueryLanguage() {
    return new QueryLanguageImpl();
  }

}
