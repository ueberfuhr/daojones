package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.spi.cache.CacheFactory;

/**
 * Default implementation of a cache factory model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class CacheFactoryModelImpl extends FactoryModelImpl<CacheFactory> implements CacheFactoryModel {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   * 
   * @param id
   *          the id
   * @param clazz
   *          the implementation class
   */
  public CacheFactoryModelImpl( final String id, final Class<? extends CacheFactory> clazz ) {
    super( id, clazz );
  }

}
