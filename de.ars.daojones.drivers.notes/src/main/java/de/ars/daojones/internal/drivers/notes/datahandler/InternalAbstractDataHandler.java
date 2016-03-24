package de.ars.daojones.internal.drivers.notes.datahandler;

import java.util.logging.Level;

import de.ars.daojones.drivers.notes.AbstractDataHandler;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;

public abstract class InternalAbstractDataHandler<T, D> extends AbstractDataHandler<T, D> {

  private static final Messages bundle = Messages.create( "DataHandler" );

  public String getMessage( final String key, final Object... params ) {
    return InternalAbstractDataHandler.bundle.get( key, params );
  }

  public void logMessage( final Level level, final String key, final Object... params ) {
    InternalAbstractDataHandler.bundle.log( level, key, params );
  }

}
