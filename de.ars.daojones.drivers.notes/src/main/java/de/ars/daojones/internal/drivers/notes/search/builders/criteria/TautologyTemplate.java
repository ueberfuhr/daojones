package de.ars.daojones.internal.drivers.notes.search.builders.criteria;

import de.ars.daojones.runtime.query.Tautology;

public class TautologyTemplate extends AbstractTemplate<Tautology> {

  @Override
  public Class<? extends Tautology> getKeyType() {
    return Tautology.class;
  }

  @Override
  protected String getKey( final QueryContext<Tautology> context ) {
    return context.getSearchType().getValue().concat( ".logical.true" );
  }

}
