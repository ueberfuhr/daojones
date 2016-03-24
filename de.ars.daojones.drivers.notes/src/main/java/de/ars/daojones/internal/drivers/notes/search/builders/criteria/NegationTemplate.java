package de.ars.daojones.internal.drivers.notes.search.builders.criteria;

import de.ars.daojones.runtime.query.Negation;

public class NegationTemplate extends AbstractTemplate<Negation> {

  @Override
  public Class<? extends Negation> getKeyType() {
    return Negation.class;
  }

  @Override
  protected String getKey( final QueryContext<Negation> context ) {
    return context.getSearchType().getValue().concat( ".logical.not" );
  }

}
