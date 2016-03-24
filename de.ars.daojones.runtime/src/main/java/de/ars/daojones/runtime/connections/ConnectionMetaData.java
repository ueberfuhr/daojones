package de.ars.daojones.runtime.connections;

/**
 * Returns some metadata about a connection.
 * 
 * @param <T>
 *          The Dao class the connection is responsible for.
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 1.2
 */
public interface ConnectionMetaData<T> {

  /**
   * Returns whether updating objects is allowed or not.
   * 
   * @param t
   *          the Dao that should be operated by the connection
   * @return true, if updating objects is allowed
   */
  public boolean isUpdateAllowed( T... t );

  /**
   * Returns whether deleting objects is allowed or not.
   * 
   * @param t
   *          the Dao that should be operated by the connection
   * @return true, if deleting objects is allowed
   */
  public boolean isDeleteAllowed( T... t );

}
