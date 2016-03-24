package de.ars.daojones.drivers.notes.search;

import de.ars.daojones.drivers.notes.Aware;

/**
 * A common interface for an object that acts dependent from the search type for
 * the current request.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 * @param <T>
 *          the key type
 */
public interface SearchTypeAware<T> extends Aware<T> {

  /**
   * Returns the type of the search.
   * 
   * @return the type of the search
   */
  SearchType getSearchType();

}
