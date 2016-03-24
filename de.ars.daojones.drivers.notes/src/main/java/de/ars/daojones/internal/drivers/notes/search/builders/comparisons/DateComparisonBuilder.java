package de.ars.daojones.internal.drivers.notes.search.builders.comparisons;

import java.util.Date;

import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder.QueryContext;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.query.DateComparison;
import de.ars.daojones.runtime.query.FieldComparison;

public class DateComparisonBuilder extends AbstractComparisonBuilder<Date> {

  private static final Messages bundle = Messages.create( "search.builders.comparisons" );

  @Override
  public Class<? extends DateComparison> getKeyType() {
    return DateComparison.class;
  }

  @Override
  public Class<? extends Date> getFieldType() {
    return Date.class;
  }

  @Override
  protected String getKey( final ComparisonContext<Date> context ) throws QueryLanguageException {
    final QueryContext<FieldComparison<Date>> qc = context.getQueryContext();
    final DateComparison comparison = ( DateComparison ) qc.getCriterion().getComparison();
    if ( comparison.isIncludingTime() && SearchType.FT_SEARCH == qc.getSearchType() ) {
      // Notes FTSearch is not able to search for time stamps
      final String message = DateComparisonBuilder.bundle.get( "error.view.datetime" );
      throw new QueryLanguageException( qc.getCriterion(), qc.getModel(), message );
    }
    switch ( comparison ) {
    case DATE_EQUALS:
    case TIME_EQUALS:
      return "datetime.equals";
    case DATE_BEFORE:
    case TIME_BEFORE:
      return "datetime.before";
    case DATE_AFTER:
    case TIME_AFTER:
      return "datetime.after";
    default:
      throw new QueryLanguageException( qc.getCriterion(), qc.getModel() );
    }
  }

}
