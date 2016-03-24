package de.ars.daojones.connections;

import java.io.Serializable;

import de.ars.daojones.cache.CacheValue;
import de.ars.daojones.cache.RepeatableCommand;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.query.Query;

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
abstract class AccessorCommand<V, T extends Dao> implements
    RepeatableCommand<CacheValue<V>>, Serializable {

  private static final long serialVersionUID = 4165475938059710047L;
  // private static final Logger logger =
  // Logger.getLogger(AccessorCommand.class
  // .getName());

  private transient Accessor<T> accessor;
  private long cacheInterval;
  private String applicationContextId;
  private Class<T> theGenericClass;
  private Query parameters;
  private Object[] arguments;
  private boolean executed = false;

  /**
   * Creates an instance based on an accessor.
   * 
   * @param theGenericClass
   *          the bean class
   * @param cacheInterval
   *          the cache interval
   * @param parameters
   *          the search parameters
   * @param accessor
   *          the accessor
   * @param arguments
   *          some unspecified arguments
   */
  public AccessorCommand( Class<T> theGenericClass, long cacheInterval,
      Query parameters, Accessor<T> accessor, Object... arguments ) {
    this(
        theGenericClass,
        cacheInterval,
        parameters,
        null != accessor ? accessor.getConnection().getContext().getId() : null,
        arguments );
    this.accessor = accessor;
  }

  /**
   * Creates an instance based on accessor data.
   * 
   * @param theGenericClass
   *          the bean class
   * @param cacheInterval
   *          the cache interval
   * @param parameters
   *          the search parameters
   * @param applicationContextId
   *          the id of the {@link ApplicationContext}
   * @param arguments
   *          some unspecified arguments
   */
  public AccessorCommand( Class<T> theGenericClass, long cacheInterval,
      Query parameters, String applicationContextId, Object... arguments ) {
    super();
    this.applicationContextId = applicationContextId;
    this.theGenericClass = theGenericClass;
    this.parameters = parameters;
    this.cacheInterval = cacheInterval;
    this.arguments = arguments;
  }

  /**
   * Returns the {@link Accessor}.
   * 
   * @return the {@link Accessor}
   * @throws DataAccessException
   */
  protected Accessor<T> getAccessor() throws DataAccessException {
    synchronized ( this ) {
      if ( null == this.accessor && null != this.applicationContextId
          && null != this.theGenericClass ) {
        final ApplicationContext ctx = ApplicationContextFactory.getInstance()
            .getApplicationContext( this.applicationContextId );
        if ( null == ctx )
          return null;
        final Connection<T> con = Connection.get( ctx, this.theGenericClass );
        if ( null == con )
          return null;
        this.accessor = con.getAccessor();
        while ( null != this.accessor
            && this.accessor instanceof CachedAccessor ) {
          this.accessor = ( ( CachedAccessor<T> ) this.accessor )
              .getUncachedAccessor();
        }
      }
    }
    return this.accessor;
  }

  protected Query getParameters() {
    return this.parameters;
  }

  protected long getCacheInterval() {
    return this.cacheInterval;
  }

  protected Object[] getArguments() {
    return this.arguments;
  }

  public boolean isExecuted() {
    return executed;
  }

  protected void setExecuted( boolean executed ) {
    this.executed = executed;
  }

}
