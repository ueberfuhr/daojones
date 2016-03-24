package de.ars.daojones.drivers.notes;

/**
 * A provider to find a data handler for a given data type.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface DataHandlerProvider {

  /**
   * Finds a provider for a given data type.
   * 
   * @param <T>
   *          the data type
   * @param c
   *          the class object for the data type
   * @return the data handler
   * @throws DataHandlerNotFoundException
   *           if a data handler for this type could not be found
   */
  <T> DataHandler<T> findProvider( Class<T> c ) throws DataHandlerNotFoundException;

}
