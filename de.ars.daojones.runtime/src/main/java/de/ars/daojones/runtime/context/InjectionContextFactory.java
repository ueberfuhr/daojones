package de.ars.daojones.runtime.context;

import de.ars.daojones.internal.runtime.configuration.context.DaoJonesContextImpl;
import de.ars.daojones.internal.runtime.configuration.context.DatabaseAccessorProvidingContext;
import de.ars.daojones.internal.runtime.connections.AbstractAccessor;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;

/**
 * A class that provides method to create instances of InjectionContext.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class InjectionContextFactory {

  private static final Messages bundle = Messages.create( "context.InjectionContextFactory" );

  private InjectionContextFactory() {
    super();
  }

  public static InjectionContext createFromBean( final Object bean ) {
    return new DatabaseAccessorProvidingContext() {

      @Override
      public DatabaseAccessor getDb( final DaoJonesContextImpl context, final String applicationId )
              throws DataAccessException, ConfigurationException {
        final Class<? extends Object> beanClass = bean.getClass();
        final BeanModelManager bmm = context.getConfiguration().getBeanModelManager();
        final BeanModel model = bmm.getEffectiveModel( applicationId, beanClass );
        final BeanAccessor beanAccessor = context.getBeanAccessor();
        final Identificator identificator = beanAccessor.getIdentificator( model, bean );
        final ConnectionProvider cp = context.getApplication( applicationId );
        final Connection<?> con = cp.getConnection( beanClass );
        final Object id = null != identificator ? identificator.getId( applicationId ) : null;
        if ( null != id ) {
          return ( ( AbstractAccessor<?> ) con ).doFindById( identificator.getId( applicationId ) );
        } else {
          throw new ConfigurationException(
                  InjectionContextFactory.bundle.get( "error.noid", bean.getClass().getName() ) );
        }
      }
    };
  }
}
