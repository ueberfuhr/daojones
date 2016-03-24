package de.ars.daojones.internal.runtime.connections;

import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.connections.ConnectionMetaData;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorContext;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorProvider;
import de.ars.daojones.runtime.spi.database.Connection;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;
import de.ars.daojones.runtime.spi.database.DriverProvider;

public class ConnectionImpl<T> extends AbstractConnection<T> implements DriverProvider<T>, BeanAccessorProvider {

  private final Connection<T> driver;

  public ConnectionImpl( final ConnectionContext<T> context, final Connection<T> driver ) {
    super( context );
    this.driver = driver;
  }

  @Override
  public Connection<T> getDriver() {
    return driver;
  }

  // Just increase the visibility
  @Override
  public BeanAccessor getBeanAccessor() {
    return super.getBeanAccessor();
  }

  // Just increase the visibility
  @Override
  public BeanAccessorContext<T> createBeanAccessorContext( final DatabaseEntry t, final Class<? extends T> type ) {
    return super.createBeanAccessorContext( t, type );
  }

  // Just increase the visibility
  @Override
  public Class<? extends T> getBeanClass( final BeanModel model ) throws ClassNotFoundException {
    return super.getBeanClass( model );
  }

  @Override
  public ConnectionMetaData<T> getMetaData() {
    return getDriver().getMetaData();
  }

  @Override
  public DatabaseEntry doFindById( final Object id ) throws DataAccessException {
    return getDriver().doFindById( id );
  }

  @Override
  protected DatabaseEntry doCreate( final BeanModel model ) throws DataAccessException {
    return getDriver().doCreate( model );
  }

  @Override
  protected SearchResult<DatabaseEntry> executeQuery( final Query query, final BeanModel... beanModels )
          throws DataAccessException {
    return getDriver().doFind( query, beanModels );
  }

  @Override
  protected void doEventCloseConnection() throws DataAccessException {
    getDriver().close();
  }

}
