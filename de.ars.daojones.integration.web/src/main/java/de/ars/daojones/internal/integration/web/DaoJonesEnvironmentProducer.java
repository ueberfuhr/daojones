package de.ars.daojones.internal.integration.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.InjectionPoint;

import de.ars.daojones.integration.web.DaoJonesEnvironment;
import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.Application;

/**
 * CDI Producer class to get DaoJones environment for this web application.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 */
//application scope: important!!!
@ApplicationScoped
@ManagedBean( DaoJonesEnvironmentProducer.BEAN_NAME )
public class DaoJonesEnvironmentProducer {

  static final String BEAN_NAME = "dj.internal.DaoJonesEnvironmentProducer";

  private static final Messages bundle = Messages.create( "DaoJonesEnvironmentProducer" );

  private DaoJonesEnvironment environment;

  // Invoked by the servlet context listener during application startup
  public void setEnvironment( final DaoJonesEnvironment environment ) {
    this.environment = environment;
  }

  @Produces
  @Default
  public DaoJonesEnvironment getEnvironment() {
    return environment;
  }

  @Produces
  @Default
  // ConnectionProvider
  public Application getApplicationOrConnectionProvider() {
    return getEnvironment().getApplication();
  }

  @Produces
  @Default
  @Typed( { Connection.class, Accessor.class } )
  // ConnectionProvider
  public Connection<?> getConnection( final InjectionPoint injectionPoint ) {
    try {
      final Application app = getApplicationOrConnectionProvider();
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

  public void closeConnection( @Observes @Default final Connection<?> connection ) {
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
