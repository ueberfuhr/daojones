package de.ars.daojones.drivers.notes.xt;

import lotus.domino.Base;
import lotus.domino.NotesException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * This handler updates the full-text index of the database before searching any
 * view. The index can only be updated for the whole database, not for a single
 * view.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class UpdateIndexHandler extends NotesEventHandlerAdapter {

  private final boolean create;

  /**
   * Constructor. The create flag is set to <tt>false</tt>.
   * 
   * @see #UpdateIndexHandler(boolean)
   */
  public UpdateIndexHandler() {
    this( false );
  }

  /**
   * Constructor.
   * 
   * @param create
   *          Specify <tt>true</tt> if you want to create an index if none
   *          exists (valid only for local databases). Otherwise, specify
   *          <tt>false</tt>.
   */
  public UpdateIndexHandler( final boolean create ) {
    super();
    this.create = create;
  }

  /**
   * Returns the create flag. If <tt>true</tt>, the handler creates an index if
   * none exists (valid only for local databases) during the index update.
   * 
   * @return the create flag
   */
  public boolean isCreate() {
    return create;
  }

  @Override
  public void beforeFTSearch( final NotesEventHandlerContext<? extends Base> context, final FTSearchConfiguration config )
          throws NotesException, DataAccessException, ConfigurationException {
    context.getDatabase().updateFTIndex( isCreate() );
  }

}
