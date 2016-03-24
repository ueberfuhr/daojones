package de.ars.daojones.internal.drivers.notes.search.builders.comparisons;

import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder.QueryContext;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.query.FieldComparison;
import de.ars.daojones.runtime.query.StringComparison;

public class StringComparisonBuilder extends AbstractComparisonBuilder<String> {

  private static final Messages bundle = Messages.create( "search.builders.comparisons" );

  @Override
  public Class<? extends StringComparison> getKeyType() {
    return StringComparison.class;
  }

  @Override
  public Class<? extends String> getFieldType() {
    return String.class;
  }

  @Override
  protected String getKey( final ComparisonContext<String> context ) throws QueryLanguageException {
    final QueryContext<FieldComparison<String>> qc = context.getQueryContext();
    final StringComparison comparison = ( StringComparison ) qc.getCriterion().getComparison();
    if ( comparison.isCaseSensitive() && SearchType.FT_SEARCH == qc.getSearchType() ) {
      throw new QueryLanguageException( qc.getCriterion(), qc.getModel(),
              StringComparisonBuilder.bundle.get( "error.view.string" ) );
    } else {
      switch ( comparison ) {
      case EQUALS:
        return "string.equals";
      case EQUALS_IGNORECASE:
        return "string.equals.nocase";
      case STARTSWITH:
        return "string.starts";
      case STARTSWITH_IGNORECASE:
        return "string.starts.nocase";
      case ENDSWITH:
        return "string.ends";
      case ENDSWITH_IGNORECASE:
        return "string.ends.nocase";
      case CONTAINS:
        return "string.contains";
      case CONTAINS_IGNORECASE:
        return "string.contains.nocase";
      case LIKE:
        return "string.like";
      case LIKE_IGNORECASE:
        return "string.like.nocase";
      default:
        throw new QueryLanguageException( qc.getCriterion(), qc.getModel() );
      }
    }
  }

}
