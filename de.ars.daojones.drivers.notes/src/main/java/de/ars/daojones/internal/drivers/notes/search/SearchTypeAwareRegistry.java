package de.ars.daojones.internal.drivers.notes.search;

import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.drivers.notes.search.SearchTypeAware;
import de.ars.daojones.internal.drivers.notes.AwareNotFoundException;
import de.ars.daojones.internal.drivers.notes.AwareRegistry;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;

public class SearchTypeAwareRegistry<K, T extends SearchTypeAware<K>> {

  private static final Messages bundle = Messages.create( "aware.searchtype.registry" );

  private final AwareRegistry<K, T> forFormula;
  private final AwareRegistry<K, T> forFTSearch;

  private final Class<? extends T> awareType;

  public SearchTypeAwareRegistry( final Class<? extends T> awareType ) {
    super();
    this.awareType = awareType;
    this.forFormula = new AwareRegistry<K, T>( awareType ) {
      @Override
      protected boolean isAware( final Class<? extends K> keyType, final T aware ) {
        final SearchType searchType = aware.getSearchType();
        return null == searchType || searchType == SearchType.FORMULA;
      }
    };
    this.forFTSearch = new AwareRegistry<K, T>( awareType ) {
      @Override
      protected boolean isAware( final Class<? extends K> keyType, final T aware ) {
        final SearchType searchType = aware.getSearchType();
        return null == searchType || searchType == SearchType.FT_SEARCH;
      }
    };
  }

  public T findAware( final Class<? extends K> keyType, final SearchType searchType ) {
    final AwareRegistry<K, T> registry = searchType == SearchType.FORMULA ? forFormula : forFTSearch;
    return registry.findAware( keyType );
  }

  public T findAwareNotNull( final Class<? extends K> keyType, final SearchType searchType )
          throws AwareNotFoundException {
    final T result = findAware( keyType, searchType );
    if ( null != result ) {
      return result;
    } else {
      // I18n
      final String key = "error.aware.missing";
      final Object p0 = awareType.getName();
      final Object p1 = keyType.getName();
      final Object p2 = searchType.name();
      final String message = SearchTypeAwareRegistry.bundle.get( key, p0, p1, p2 );
      throw new AwareNotFoundException( message );
    }
  }

}
