package de.ars.daojones.runtime.test.spi.database;

import java.io.Serializable;

import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.Entry;

/**
 * An index to search the test model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface TestModelIndex {

  /**
   * Finds the corresponding data source for a bean model.
   * 
   * @param beanModel
   *          the bean model
   * @param create
   *          a flag indicating whether to create the data source or not, if it
   *          doesn't already exist.
   * @return the data source or <tt>null</tt>, if not exising and
   *         <tt>create</tt> is set to <tt>false</tt>
   */
  DataSource getDataSource( final BeanModel beanModel, final boolean create );

  /**
   * Finds the corresponding bean model for a given data source.
   * 
   * @param ds
   *          the data source
   * @return the bean model
   * @throws ConfigurationException
   *           if a bean model could not be found or not be loaded
   */
  BeanModel findBeanModel( final DataSource ds ) throws ConfigurationException;

  /**
   * Creates an identificator that refers to the given entry.
   * 
   * @param entry
   *          the entry
   * @return the identificator
   */
  Identificator createIdentificator( final Entry entry );

  /**
   * Finds an entry for a given id.
   * 
   * @param id
   *          the id
   * @return the entry
   * @throws ConfigurationException
   */
  Entry findEntry( Serializable id ) throws ConfigurationException;

  /**
   * Returns the application id.
   * 
   * @return the application id
   */
  String getApplication();
}