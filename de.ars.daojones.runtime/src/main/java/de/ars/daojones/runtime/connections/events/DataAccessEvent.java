package de.ars.daojones.runtime.connections.events;

import java.util.Date;

import de.ars.daojones.runtime.connections.Accessor;

/**
 * The object providing information about an event that is noticed by a
 * {@link DataAccessListener}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 * @param <T>
 *          the type of the DaoJones bean
 */
public class DataAccessEvent<T> {

  private final DataAccessType accessType;
  private final Accessor<T> accessor;
  private final T[] beans;
  private final Object accessData;

  /**
   * Creates an instance.
   * 
   * @param accessType
   *          the {@link DataAccessType}
   * @param accessor
   *          the {@link Accessor}
   * @param beans
   *          the beans that are handled
   * @param accessData
   *          an unspecified data object that is not used per default
   */
  public DataAccessEvent( final DataAccessType accessType, final Accessor<T> accessor, final T[] beans,
          final Object accessData ) {
    super();
    this.accessType = accessType;
    this.accessor = accessor;
    this.accessData = accessData;
    this.beans = beans;
  }

  /**
   * Creates an instance.
   * 
   * @param accessType
   *          the {@link DataAccessType}
   * @param accessor
   *          the {@link Accessor}
   * @param beans
   *          the beans that are handled
   */
  public DataAccessEvent( final DataAccessType accessType, final Accessor<T> accessor, final T[] beans ) {
    this( accessType, accessor, beans, accessType.toString() + "@" + new Date().getTime() );
  }

  /**
   * Returns the beans that are handled.
   * 
   * @return the bean the bean or <code>null</code>, if the access type is not
   *         update or delete
   */
  public T[] getBeans() {
    return beans;
  }

  /**
   * Returns some data for this event. Per default, this object is not used.
   * 
   * @return the event data
   */
  public Object getAccessData() {
    return accessData;
  }

  /**
   * Returns the {@link DataAccessType}.
   * 
   * @return the {@link DataAccessType}
   */
  public DataAccessType getAccessType() {
    return accessType;
  }

  /**
   * Returns the {@link Accessor}.
   * 
   * @return the {@link Accessor}
   */
  public Accessor<T> getAccessor() {
    return accessor;
  }

}
