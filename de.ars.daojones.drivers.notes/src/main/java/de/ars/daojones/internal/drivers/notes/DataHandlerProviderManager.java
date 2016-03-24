package de.ars.daojones.internal.drivers.notes;

import de.ars.daojones.drivers.notes.DataHandler;
import de.ars.daojones.drivers.notes.DataHandlerNotFoundException;
import de.ars.daojones.drivers.notes.DataHandlerProvider;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;

public class DataHandlerProviderManager implements DataHandlerProvider {

  private static final Messages bundle = Messages.create( "datahandler.Provider" );

  private static DataHandlerProvider theInstance;

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  private final AwareRegistry<Object, DataHandler<Object>> dataHandlers = new AwareRegistry<Object, DataHandler<Object>>(
          ( Class ) DataHandler.class );

  private DataHandlerProviderManager() {
    super();
  }

  public static synchronized DataHandlerProvider getInstance() {
    if ( null == DataHandlerProviderManager.theInstance ) {
      DataHandlerProviderManager.theInstance = new DataHandlerProviderManager();
    }
    return DataHandlerProviderManager.theInstance;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public <T> DataHandler<T> findProvider( final Class<T> c ) throws DataHandlerNotFoundException {
    try {
      return ( DataHandler<T> ) dataHandlers.findAwareNotNull( c );
    } catch ( final AwareNotFoundException e ) {
      // original exception is suppressed because of a custom error message
      throw new DataHandlerNotFoundException( this, c, DataHandlerProviderManager.bundle.get(
              "datahandler.instructions", c.getName() ) );
    }
  }

}
