package de.ars.daojones.internal.drivers.notes.search.builders.comparisons;

import java.util.Collection;

import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder.QueryContext;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.query.CollectionComparison;
import de.ars.daojones.runtime.query.FieldComparison;

@SuppressWarnings( "rawtypes" )
public class CollectionComparisonBuilder extends AbstractComparisonBuilder<Collection> {

  private static final Messages bundle = Messages.create( "search.builders.comparisons" );

  @Override
  public Class<? extends CollectionComparison> getKeyType() {
    return CollectionComparison.class;
  }

  @Override
  public Class<? extends Collection> getFieldType() {
    return Collection.class;
  }

  @Override
  protected String getKey( final ComparisonContext<Collection> context ) throws QueryLanguageException {
    final QueryContext<FieldComparison<Collection>> qc = context.getQueryContext();
    final CollectionComparison comparison = ( CollectionComparison ) qc.getCriterion().getComparison();
    if ( SearchType.FT_SEARCH == qc.getSearchType() ) {
      throw new QueryLanguageException( qc.getCriterion(), qc.getModel(),
              CollectionComparisonBuilder.bundle.get( "error.view.collection" ) );
    } else {
      switch ( comparison ) {
      case EQUALS:
        return "collection.equals";
      case CONTAINS_ONE:
        return "collection.contains.one";
      case CONTAINS_ALL:
        return "collection.contains.all";
      default:
        throw new QueryLanguageException( qc.getCriterion(), qc.getModel() );
      }
    }
  }

}
