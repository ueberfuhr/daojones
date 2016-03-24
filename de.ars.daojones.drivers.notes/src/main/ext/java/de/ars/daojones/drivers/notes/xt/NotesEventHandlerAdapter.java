package de.ars.daojones.drivers.notes.xt;

import lotus.domino.Base;
import lotus.domino.Document;
import lotus.domino.NotesException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * An adapter class for the {@link NotesEventHandler} interface. It is
 * recommended to use this class as a super class for custom
 * {@link NotesEventHandler} implementations and overwrite the empty implemented
 * methods to provide a better compatibility in case of interface extensions in
 * the future.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public/*not abstract!!!*/class NotesEventHandlerAdapter implements NotesEventHandler {

  // Do not make this type abstract to get interface extensions as a compile-time error.

  /**
   * Default constructor.
   */
  protected NotesEventHandlerAdapter() {
    super();
  }

  @Override
  public void beforeSave( final NotesEventHandlerContext<Document> context, final DocumentSaveConfiguration config )
          throws NotesException, DataAccessException, ConfigurationException {
    // empty stub
  }

  @Override
  public void afterSave( final NotesEventHandlerContext<Document> context, final DocumentSaveResult result ) {
    // empty stub
  }

  @Override
  public void beforeDelete( final NotesEventHandlerContext<Document> context, final DocumentDeleteConfiguration config )
          throws NotesException, DataAccessException, ConfigurationException {
    // empty stub
  }

  @Override
  public void afterDelete( final NotesEventHandlerContext<Document> context, final DocumentDeleteResult result ) {
    // empty stub
  }

  @Override
  public void beforeFTSearch( final NotesEventHandlerContext<? extends Base> context, final FTSearchConfiguration config )
          throws NotesException, DataAccessException, ConfigurationException {
    // empty stub
  }

  @Override
  public void afterFTSearch( final NotesEventHandlerContext<? extends Base> context, final FTSearchResult result ) {
    // empty stub
  }

}
