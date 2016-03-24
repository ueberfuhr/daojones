package de.ars.daojones.internal.runtime.connections;

import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.connections.events.DataAccessListener;
import de.ars.daojones.runtime.query.Query;

/**
 * A wrapper for an accessorn that delegates to an internal getDelegate().
 * Clients may subclass this class.
 * 
 * @param <T>
 *          The DaoJones DAO bean that can be read or written with this
 *          connection.
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 1.1.0
 */
public class AccessorWrapper<T> implements Accessor<T> {

  private final Accessor<T> delegate;

  /**
   * Creates an instance.
   * 
   * @param delegate
   *          the delegate
   */
  public AccessorWrapper( final Accessor<T> delegate ) {
    this.delegate = delegate;
  }

  /**
   * Returns the delegate connection.
   * 
   * @return the delegate connection
   */
  protected Accessor<T> getDelegate() {
    return this.delegate;
  }

  @Override
  public void update( final T... t ) throws DataAccessException {
    getDelegate().update( t );
  }

  @Override
  public void delete( final T... t ) throws DataAccessException {
    getDelegate().delete( t );
  }

  @Override
  public void delete( final Query query ) throws DataAccessException {
    getDelegate().delete( query );
  }

  @Override
  public de.ars.daojones.runtime.connections.Accessor.SearchResult<T> findAll( final Query query )
          throws DataAccessException {
    return getDelegate().findAll( query );
  }

  @Override
  public T find() throws DataAccessException {
    return getDelegate().find();
  }

  @Override
  public T find( final Query query ) throws DataAccessException {
    return getDelegate().find( query );
  }

  @Override
  public T findById( final Object id ) throws DataAccessException {
    return getDelegate().findById( id );
  }

  @Override
  public void close() throws DataAccessException {
    getDelegate().close();
  }

  @Override
  public boolean addDataAccessListener( final DataAccessListener<T> listener ) {
    return getDelegate().addDataAccessListener( listener );
  }

  @Override
  public boolean removeDataAccessListener( final DataAccessListener<T> listener ) {
    return getDelegate().removeDataAccessListener( listener );
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "AccessorWrapper [accessor=" ).append( getDelegate() ).append( "]" );
    return builder.toString();
  }

  @Override
  public BeanModel getBeanModel() {
    return getDelegate().getBeanModel();
  }

}
