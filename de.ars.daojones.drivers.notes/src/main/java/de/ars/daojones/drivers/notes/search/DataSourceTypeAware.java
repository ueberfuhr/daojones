package de.ars.daojones.drivers.notes.search;

import de.ars.daojones.drivers.notes.Aware;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;

/**
 * A common interface for an object that acts dependent from the data source
 * type for the current request.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the key type
 */
public interface DataSourceTypeAware<T> extends Aware<T> {

  /**
   * Returns the type of the data source.
   * 
   * @return the type of the data source
   */
  DataSourceType getDatasourceType();

}
