package de.ars.daojones.runtime.query;

import java.io.Serializable;

import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * A search criterion is used to find objects based on special conditions. You
 * can use the {@link SearchCriterionBuilder} class to create search criteria.
 * 
 * <p>
 * <i>Since DaoJones 2.0</i>, search criteria are designed as extendable, i.e.
 * you can implement custom search criteria.
 * </p>
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 1.0
 * @see SearchCriterionBuilder
 */
public interface SearchCriterion extends Serializable {

  /**
   * @see Object#equals(Object)
   */
  @Override
  public boolean equals( Object o );

  /**
   * @see Object#hashCode()
   */
  @Override
  public int hashCode();

  /**
   * Checks the criterion for a given bean.
   * 
   * @param ctx
   *          the application context
   * @param bean
   *          the bean
   * @return <tt>true</tt>, if the bean matches this criterion
   * @throws ConfigurationException
   *           if the bean is not configured within the application context
   * @throws FieldAccessException
   *           if accessing the field occured an error
   * @throws DataAccessException
   *           if accessing the database occured an error
   * @since 2.0
   */
  public boolean matches( ApplicationContext ctx, Object bean ) throws ConfigurationException, FieldAccessException,
          DataAccessException;

  /**
   * Invokes the callback methods of the visitor.
   * 
   * @param visitor
   *          the visitor
   * @since 2.0
   */
  public void accept( SearchCriterionVisitor visitor );

}
