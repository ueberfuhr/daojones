package de.ars.daojones.internal.drivers.notes.search.builders.criteria;

import de.ars.daojones.runtime.query.IsEmpty;

public class IsEmptyTemplate extends AbstractFieldSearchTemplate<IsEmpty> {

  @Override
  public Class<? extends IsEmpty> getKeyType() {
    return IsEmpty.class;
  }

  @Override
  protected String getKey( final QueryContext<IsEmpty> context ) {
    return context.getSearchType().getValue().concat( ".empty" );
  }

}
