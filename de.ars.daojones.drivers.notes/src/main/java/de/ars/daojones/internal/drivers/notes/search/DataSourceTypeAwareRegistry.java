package de.ars.daojones.internal.drivers.notes.search;

import de.ars.daojones.drivers.notes.search.DataSourceTypeAware;
import de.ars.daojones.internal.drivers.notes.AwareNotFoundException;
import de.ars.daojones.internal.drivers.notes.AwareRegistry;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;

public class DataSourceTypeAwareRegistry<K, T extends DataSourceTypeAware<K>> {

  private static final Messages bundle = Messages.create( "aware.datasourcetype.registry" );

  private final AwareRegistry<K, T> forTables;
  private final AwareRegistry<K, T> forViews;

  private final Class<? extends T> awareType;

  public DataSourceTypeAwareRegistry( final Class<? extends T> awareType ) {
    super();
    this.awareType = awareType;
    this.forTables = new AwareRegistry<K, T>( awareType ) {
      @Override
      protected boolean isAware( final Class<? extends K> keyType, final T aware ) {
        final DataSourceType dsType = aware.getDatasourceType();
        return null == dsType || dsType == DataSourceType.TABLE;
      }
    };
    this.forViews = new AwareRegistry<K, T>( awareType ) {
      @Override
      protected boolean isAware( final Class<? extends K> keyType, final T aware ) {
        final DataSourceType dsType = aware.getDatasourceType();
        return null == dsType || dsType == DataSourceType.VIEW;
      }
    };
  }

  public T findAware( final Class<? extends K> keyType, final DataSourceType dsType ) {
    final AwareRegistry<K, T> registry = dsType == DataSourceType.TABLE ? forTables : forViews;
    return registry.findAware( keyType );
  }

  public T findAwareNotNull( final Class<? extends K> keyType, final DataSourceType dsType )
          throws AwareNotFoundException {
    final T result = findAware( keyType, dsType );
    if ( null != result ) {
      return result;
    } else {
      // I18n
      final String key = "error.aware.missing";
      final Object p0 = awareType.getName();
      final Object p1 = keyType.getName();
      final Object p2 = dsType.name();
      final String message = DataSourceTypeAwareRegistry.bundle.get( key, p0, p1, p2 );
      throw new AwareNotFoundException( message );
    }
  }

}
