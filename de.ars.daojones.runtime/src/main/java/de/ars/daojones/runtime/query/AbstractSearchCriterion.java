package de.ars.daojones.runtime.query;

import de.ars.daojones.internal.runtime.configuration.context.ApplicationContextImpl;
import de.ars.daojones.internal.runtime.configuration.context.DaoJonesContextImpl;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;

/**
 * A super class for search criteria that implements the methods of
 * {@link SearchCriterion}. For custom search criteria, you can create
 * subclasses of {@link AbstractSearchCriterion} and override its methods.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class AbstractSearchCriterion implements SearchCriterion {

  private static final long serialVersionUID = 1L;

  private static final Messages logger = Messages.create( "query.SearchCriterion" );

  @Override
  public void accept( final SearchCriterionVisitor visitor ) {
    final boolean doChildren = visitor.preVisit( this );
    try {
      if ( doChildren ) {
        acceptChildren( visitor );
      }
    } finally {
      visitor.postVisit( this );
    }
  }

  /**
   * Invokes the {@link #accept(SearchCriterionVisitor)} method of the child
   * criteria. Override this method if your custom criterion contains child
   * criteria. This method is only invoked of the
   * {@link SearchCriterionVisitor#preVisit(SearchCriterion)} method returned a
   * value of <tt>true</tt>.
   * 
   * @param visitor
   *          the visitor
   */
  protected void acceptChildren( final SearchCriterionVisitor visitor ) {
    // empty
  }

  /**
   * Reads the value of the field. The field value is the database value, not
   * the Java property value. This can be used to compare field values, e.g.
   * during the {@link #matches(ApplicationContext, Object)} method.
   * 
   * @param ctx
   *          the application context
   * @param bean
   *          the bean
   * @param field
   *          the name of the field
   * @return the value of the field
   * @throws ConfigurationException
   *           if a bean model is not configured or if a field with the given id
   *           could not be found or is write-only
   * @throws FieldAccessException
   *           if accessing the field occurs an error
   */
  protected Object getFieldValue( final ApplicationContext ctx, final Object bean, final String field )
          throws ConfigurationException, FieldAccessException {
    final BeanAccessor beanAccessor = ctx.getBeanAccessorProvider().getBeanAccessor();
    return beanAccessor.getDatabaseValue( ctx, bean, field );
  }

  /**
   * Finds the DaoJones context.
   * 
   * @param ctx
   *          the application context
   * @return the DaoJones context
   */
  protected DaoJonesContextImpl getDaoJonesContext( final Application ctx ) {
    return ( ( ApplicationContextImpl ) ctx ).getDaoJonesContext();
  }

  /**
   * Finds the bean model for a bean.
   * 
   * @param ctx
   *          the application context
   * @param bean
   *          the bean
   * @return the bean model
   * @throws ConfigurationException
   *           if a bean model cannot be read in case of configuration errors
   */
  protected BeanModel getBeanModel( final ApplicationContext ctx, final Object bean ) throws ConfigurationException {
    final BeanModelManager bmm = ctx.getBeanModelManager();
    final BeanModel model = bmm.getEffectiveModel( ctx.getApplicationId(), bean.getClass() );
    if ( null == model ) {
      throw new ConfigurationException( AbstractSearchCriterion.logger.get( "error.beanModelNotFound",
              ctx.getApplicationId(), bean.getClass().getName() ) );
    } else {
      return model;
    }
  }

  /**
   * Unwraps a search criterion wrapper. This method should be invoked before
   * assigning an inner search criterion.
   * 
   * @param c
   *          the criterion wrapper
   * @return the unwrapped criterion
   */
  protected static SearchCriterion unwrap( final SearchCriterion c ) {
    SearchCriterion result = c;
    while ( result instanceof SearchCriterionWrapper ) {
      result = ( ( SearchCriterionWrapper ) result ).getUnwrapped();
    }
    return result;
  }

}
