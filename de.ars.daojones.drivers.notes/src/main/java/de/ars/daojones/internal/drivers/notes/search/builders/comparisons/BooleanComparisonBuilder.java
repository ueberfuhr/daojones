package de.ars.daojones.internal.drivers.notes.search.builders.comparisons;

import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder.QueryContext;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.runtime.query.BooleanComparison;
import de.ars.daojones.runtime.query.FieldComparison;

public class BooleanComparisonBuilder extends AbstractComparisonBuilder<Boolean> {

  @Override
  public Class<? extends BooleanComparison> getKeyType() {
    return BooleanComparison.class;
  }

  @Override
  public Class<? extends Boolean> getFieldType() {
    return Boolean.class;
  }

  @Override
  protected String getKey( final ComparisonContext<Boolean> context ) throws QueryLanguageException {
    final QueryContext<FieldComparison<Boolean>> qc = context.getQueryContext();
    final BooleanComparison comparison = ( BooleanComparison ) qc.getCriterion().getComparison();
    final Boolean value = qc.getCriterion().getValue();
    switch ( comparison ) {
    case EQUALS:
      return "boolean.equals." + Boolean.toString( null == value || value ).toLowerCase();
    default:
      throw new QueryLanguageException( qc.getCriterion(), qc.getModel() );
    }
  }

}
