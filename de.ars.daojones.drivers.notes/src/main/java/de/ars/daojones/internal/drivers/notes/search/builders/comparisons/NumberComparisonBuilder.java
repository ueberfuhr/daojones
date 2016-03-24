package de.ars.daojones.internal.drivers.notes.search.builders.comparisons;

import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder.QueryContext;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.runtime.query.FieldComparison;
import de.ars.daojones.runtime.query.NumberComparison;

public class NumberComparisonBuilder extends AbstractComparisonBuilder<Number> {

  @Override
  public Class<? extends NumberComparison> getKeyType() {
    return NumberComparison.class;
  }

  @Override
  public Class<? extends Number> getFieldType() {
    return Number.class;
  }

  @Override
  protected String getKey( final ComparisonContext<Number> context ) throws QueryLanguageException {
    final QueryContext<FieldComparison<Number>> qc = context.getQueryContext();
    final NumberComparison comparison = ( NumberComparison ) qc.getCriterion().getComparison();
    switch ( comparison ) {
    case EQUALS:
      return "number.equals";
    case LOWERTHAN:
      return "number.lower";
    case GREATERTHAN:
      return "number.greater";
    default:
      throw new QueryLanguageException( qc.getCriterion(), qc.getModel() );
    }
  }

}
