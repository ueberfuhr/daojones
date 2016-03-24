package de.ars.daojones.internal.drivers.notes.search;

import de.ars.daojones.runtime.connections.DataAccessException;

class ViewNotFoundException extends DataAccessException {

  private static final long serialVersionUID = 1L;

  public ViewNotFoundException( final String viewName ) {
    super( viewName );
  }
}
