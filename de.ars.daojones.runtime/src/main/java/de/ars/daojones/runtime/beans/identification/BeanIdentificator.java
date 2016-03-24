package de.ars.daojones.runtime.beans.identification;

import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * A common interface for an object that is responsible for identifying an
 * object within the database. This is used during the invocation of
 * {@link Connection#update(Object...)} and {@link Connection#delete(Object...)}
 * to find the corresponding entries within the database.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface BeanIdentificator {

  /**
   * Reads the identificator of a bean.
   * 
   * @param model
   *          the effective bean model
   * @param bean
   *          the bean
   * @return the identificator or <tt>null</tt>, if there isn't any
   *         identificator
   * @throws DataAccessException
   *           if reading the identificator fails
   * @throws ConfigurationException
   *           if the bean model is not configured correctly
   */
  Identificator getIdentificator( BeanModel model, Object bean ) throws DataAccessException, ConfigurationException;

  /**
   * Sets the identificator to the bean.
   * 
   * @param model
   *          the effective bean model
   * @param bean
   *          the bean
   * @param identificator
   *          the identificator
   * @throws DataAccessException
   *           if writing the identificator fails
   * @throws ConfigurationException
   *           if the bean model is not configured correctly
   */
  void setIdentificator( BeanModel model, Object bean, Identificator identificator ) throws DataAccessException,
          ConfigurationException;

}
