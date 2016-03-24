package de.ars.daojones.internal.runtime.connections;

import java.io.Serializable;

import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.cache.CacheValue;
import de.ars.daojones.runtime.spi.cache.RepeatableCommand;

/**
 * A {@link RepeatableCommand} using an accessor. This is used for caching
 * purposes.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *          the bean type
 * @param <V>
 *          the caller type
 */
abstract class AccessorCommand<V, T> implements
    RepeatableCommand<CacheValue<V>>, Serializable {

  private static final long serialVersionUID = -1L;

  private transient Accessor<T> accessor;
  private transient ConnectionProvider connectionProvider;
  private int timeout;
  private Class<T> theGenericClass;
  private Query parameters;
  private Object[] arguments;
  private boolean executed = false;

  /**
   * Creates an instance based on an accessor.
   * 
   * @param connectionProvider
   *          the {@link ConnectionProvider}
   * @param theGenericClass
   *          the bean class
   * @param timeout
   *          the cache timeout in seconds
   * @param parameters
   *          the search parameters
   * @param accessor
   *          the accessor
   * @param arguments
   *          some unspecified arguments
   */
  public AccessorCommand( final ConnectionProvider connectionProvider,
      final Class<T> theGenericClass, final int timeout,
      final Query parameters, final Accessor<T> accessor,
      final Object... arguments ) {
    this( connectionProvider, theGenericClass, timeout, parameters, arguments );
    this.accessor = accessor;
  }

  /**
   * Creates an instance based on accessor data.
   * 
   * @param connectionProvider
   *          the {@link ConnectionProvider}
   * @param theGenericClass
   *          the bean class
   * @param timeout
   *          the cache timeout in seconds
   * @param parameters
   *          the search parameters
   * @param applicationContextId
   *          the id of the {@link ApplicationContext}
   * @param arguments
   *          some unspecified arguments
   */
  public AccessorCommand( final ConnectionProvider connectionProvider,
      final Class<T> theGenericClass, final int timeout,
      final Query parameters, final Object... arguments ) {
    super();
    this.connectionProvider = connectionProvider;
    this.theGenericClass = theGenericClass;
    this.parameters = parameters;
    this.timeout = timeout;
    this.arguments = arguments;
  }

  /**
   * Returns the {@link Accessor}.
   * 
   * @param provider
   *          the {@link ConnectionProvider}
   * @return the {@link Accessor}
   * @throws DataAccessException
   */
  protected Accessor<T> getAccessor() throws DataAccessException {
    synchronized ( this ) {
      if ( null == this.accessor && null != connectionProvider
          && null != this.theGenericClass ) {
        final Connection<T> con = connectionProvider
            .getConnection( this.theGenericClass );
        if ( null == con ) {
          return null;
        }
        this.accessor = con.getAccessor();
        while ( null != this.accessor
            && this.accessor instanceof CachedAccessor ) {
          this.accessor = ( ( de.ars.daojones.internal.runtime.connections.CachedAccessor<T> ) this.accessor )
              .getUncachedAccessor();
        }
      }
    }
    return this.accessor;
  }

  protected Query getParameters() {
    return this.parameters;
  }

  protected int getTimeout() {
    return timeout;
  }

  protected Object[] getArguments() {
    return this.arguments;
  }

  public boolean isExecuted() {
    return executed;
  }

  protected void setExecuted( final boolean executed ) {
    this.executed = executed;
  }

}
