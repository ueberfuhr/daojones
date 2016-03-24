package de.ars.daojones.runtime.spi.database;

/**
 * A factory that creates connection instances. This is the central interface
 * that must be implemented by a driver.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2007
 */
public interface ConnectionFactory {

  /**
   * Creates a connection instance based on the given parameters.
   * 
   * @param context
   *          the context
   * @param <T>
   *          the type of the objects that are handled by the connection.
   * @return the connection instance
   * @throws ConnectionBuildException
   *           if creating the connection fails
   */
  public <T> Connection<T> createConnection( ConnectionContext<T> context ) throws ConnectionBuildException;

}
