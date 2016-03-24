package de.ars.daojones.internal.drivers.notes.search.builders.criteria;

import de.ars.daojones.runtime.query.LogicalCombination;
import de.ars.daojones.runtime.query.LogicalCombinationType;

public class LogicalCombinationTemplate extends AbstractTemplate<LogicalCombination> {

  @Override
  public Class<? extends LogicalCombination> getKeyType() {
    return LogicalCombination.class;
  }

  @Override
  protected String getKey( final QueryContext<LogicalCombination> context ) {
    final LogicalCombinationType combination = context.getCriterion().getType();
    return context.getSearchType().getValue().concat( ".logical." ).concat( combination.name().toLowerCase() );
  }
}
