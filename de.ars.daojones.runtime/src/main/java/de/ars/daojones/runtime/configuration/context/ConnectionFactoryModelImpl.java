package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.spi.database.ConnectionFactory;

/**
 * Default implementation of a connection factory model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class ConnectionFactoryModelImpl extends FactoryModelImpl<ConnectionFactory> implements ConnectionFactoryModel {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   * 
   * @param id
   *          the id
   * @param clazz
   *          the implementation class
   */
  public ConnectionFactoryModelImpl( final String id, final Class<? extends ConnectionFactory> clazz ) {
    super( id, clazz );
  }

}
