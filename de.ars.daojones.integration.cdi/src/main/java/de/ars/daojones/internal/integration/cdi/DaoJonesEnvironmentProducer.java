package de.ars.daojones.internal.integration.cdi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import de.ars.daojones.integration.cdi.DaoJonesApplication;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * CDI Producer class to get DaoJones environment.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 */
@ApplicationScoped
public class DaoJonesEnvironmentProducer {

  private static final Messages bundle = Messages.create( "producers" );

  @Inject
  private DaoJonesContext context;

  @Produces
  @Default
  public DaoJonesContextConfiguration getDaoJonesContextConfig() {
    return context.getConfiguration();
  }

  @Produces
  @DaoJonesApplication( "<placeholder>" )
  // per app name
  public Application getApplicationOrConnectionProvider( final InjectionPoint injectionPoint ) {
    final DaoJonesApplication app = injectionPoint.getAnnotated().getAnnotation( DaoJonesApplication.class );
    return context.getApplication( app.value() );
  }

  @Produces
  @DaoJonesApplication( "<placeholder>" )
  @Typed( { Connection.class, Accessor.class } )
  // per app name
  public Connection<?> getConnection( final InjectionPoint injectionPoint ) {
    try {
      final Application app = getApplicationOrConnectionProvider( injectionPoint );
      final Type type = injectionPoint.getType();
      if ( type instanceof ParameterizedType ) {
        final ParameterizedType pType = ( ParameterizedType ) type;
        final Type[] typeArguments = pType.getActualTypeArguments();
        if ( typeArguments.length == 1 ) { // Connection<!!!>
          final Type beanType = typeArguments[0];
          if ( beanType instanceof Class ) {
            return app.getConnection( ( Class<?> ) beanType );
          } else {
            throw new IllegalArgumentException(
                    DaoJonesEnvironmentProducer.bundle.get( "error.connection.invalid_type_parameter" ) );
          }
        } else {
          throw new IllegalArgumentException( DaoJonesEnvironmentProducer.bundle.get(
                  "error.connection.invalid_type_arguments_count", type, 1 ) );
        }
      } else {
        throw new IllegalArgumentException( DaoJonesEnvironmentProducer.bundle.get( "error.connection.raw_type", type ) );
      }
    } catch ( final DataAccessException e ) {
      throw new RuntimeException( e );
    }
  }

  public void closeConnection( @Disposes @DaoJonesApplication( "<placeholder>" ) final Connection<?> connection ) {
    if ( null != connection ) {
      try {
        connection.close();
      } catch ( final DataAccessException e ) {
        throw new RuntimeException( e );
      }
    }
  }

  @Override
  public String toString() {
    return DaoJonesEnvironmentProducer.bundle.get( "producer.env.name" );
  }

}
