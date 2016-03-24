package de.ars.daojones.runtime.test.spi.database;

import java.util.Iterator;
import java.util.List;

import de.ars.daojones.internal.runtime.configuration.context.BeanModelHelper;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.SelfDescribing;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;
import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.Entry;

public class TestDatabaseEntry extends DatabaseAccessorImpl implements DatabaseEntry, SelfDescribing {

  private static long counter;

  private final BeanModel beanModel;

  public TestDatabaseEntry( final Entry model, final BeanModel beanModel, final TestModelIndex index ) {
    super( model, index );
    this.beanModel = beanModel;
  }

  @Override
  public BeanModel getBeanModel() {
    return beanModel;
  }

  @Override
  public void store() throws DataAccessException {
    final Entry model = getModel();
    if ( null == model.getId() ) {
      synchronized ( TestDatabaseEntry.class ) {
        model.setId( "entry_" + System.currentTimeMillis() + "_" + ( TestDatabaseEntry.counter++ ) );
      }
    }
    final List<Entry> entries = getIndex().getDataSource( beanModel, true ).getEntries();
    // remove existing entry
    for ( final Iterator<Entry> it = entries.iterator(); it.hasNext(); ) {
      final Entry entry = it.next();
      if ( model.getId().equals( entry.getId() ) ) {
        it.remove();
        break;
      }
    }
    getIndex().getDataSource( beanModel, true ).getEntries().add( getModel() );
  }

  @Override
  public void delete() throws DataAccessException {
    final DataSource ds = getIndex().getDataSource( beanModel, false );
    if ( null != ds ) {
      ds.getEntries().remove( getModel() );
    }
  }

  @Override
  public DatabaseFieldMappedElement findFieldModel( final String id ) {
    final DatabaseFieldMappedElement result = BeanModelHelper.findFieldModel( getBeanModel().getBean(), id );
    return null != result ? result : BeanModelHelper.findFieldModel( getBeanModel().getBean(), id, true, true );
  }

  @Override
  public DatabaseFieldMappedElement findFieldModel( final String id, final boolean write ) {
    final DatabaseFieldMappedElement result = BeanModelHelper.findFieldModel( getBeanModel().getBean(), id, write );
    return null != result ? result : BeanModelHelper.findFieldModel( getBeanModel().getBean(), id, true, true );
  }

}
