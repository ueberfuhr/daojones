package de.ars.daojones.internal.drivers.notes;

import java.util.Collection;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.DataHandler;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.drivers.notes.DataHandlerProvider;
import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.drivers.notes.xt.NotesEventHandler;
import de.ars.daojones.drivers.notes.xt.NotesEventHandler.DocumentDeleteConfiguration;
import de.ars.daojones.drivers.notes.xt.NotesEventHandler.DocumentSaveConfiguration;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class DocumentDatabaseEntry extends AbstractBaseEntry<Document> {

  private static final Messages bundle = Messages.create( "DocumentEntry" );

  public interface NotesEventHandlerProvider {
    NotesEventHandler[] getNotesEventHandlers();
  }

  private final NotesEventHandlerProvider notesEventHandlerProvider;

  public DocumentDatabaseEntry( final Document document, final BeanModel beanModel,
          final DataHandlerProvider dataHandlerProvider, final NotesEventHandlerProvider notesEventHandlerProvider ) {
    super( document, beanModel, dataHandlerProvider );
    this.notesEventHandlerProvider = notesEventHandlerProvider;
  }

  @Override
  protected <E> E getFieldValue( final FieldContext<E> context, final DataHandler<E> handler )
          throws DataAccessException, NotesException, DataHandlerException {
    return handler.readDocument( this, context );
  }

  @Override
  protected <E> void setFieldValue( final FieldContext<E> context, final DataHandler<E> handler, final E value )
          throws DataAccessException, NotesException, DataHandlerException {
    handler.writeDocument( this, context, value );
  }

  @Override
  public Database getDatabase() throws NotesException {
    return getSource().getParentDatabase();
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public String[] getFields() throws DataAccessException {
    try {
      final Collection<Item> items = getSource().getItems();
      final String[] result = new String[items.size()];
      int index = 0;
      for ( final Item item : items ) {
        result[index] = item.getName();
        index++;
      }
      return result;
    } catch ( final NotesException e ) {
      throw new DataAccessException( e );
    }
  }

  @Override
  public String getApplication() {
    return getBeanModel().getId().getApplicationId();
  }

  @Override
  public Identificator getIdentificator() throws DataAccessException {
    try {
      return getSource().isDeleted() || getSource().isNewNote() ? null : Identificators
              .createIdentificator( getSource() );
    } catch ( final NotesException e ) {
      throw new DataAccessException( e );
    }
  }

  @Override
  public void store() throws DataAccessException {
    try {
      final Document document = getSource();
      final NotesEventHandler.DocumentSaveConfiguration handlerConfig = new NotesEventHandler.DocumentSaveConfiguration();
      handlerConfig.setForce( NotesDriverConfiguration.SAVE_FORCE );
      handlerConfig.setCreateResponse( NotesDriverConfiguration.SAVE_CREATE_RESPONSE );
      handlerConfig.setMarkRead( NotesDriverConfiguration.SAVE_MARK_READ );
      handlerConfig.setIgnoreFailure( false );
      final NotesEventHandler.NotesEventHandlerContext<Document> handlerContext = new NotesEventHandler.NotesEventHandlerContext<Document>() {
        @Override
        public Document getSource() throws NotesException {
          return document;
        }

        @Override
        public Database getDatabase() throws NotesException {
          return document.getParentDatabase();
        }
      };
      final NotesEventHandler[] handlers = notesEventHandlerProvider.getNotesEventHandlers();
      // Invoke handlers before save operation
      for ( final NotesEventHandler handler : handlers ) {
        handler.beforeSave( handlerContext, handlerConfig );
      }
      // force, createresponse, markread
      final boolean saved = document.save( handlerConfig.isForce(), handlerConfig.isCreateResponse(),
              handlerConfig.isMarkRead() );
      if ( !saved && !handlerConfig.isIgnoreFailure() ) {
        throw new DataAccessException( DocumentDatabaseEntry.bundle.get( "error.save",
                NotesDriverConfiguration.CONFIG_FILE ) );
      }
      final NotesEventHandler.DocumentSaveResult handlerResult = new NotesEventHandler.DocumentSaveResult() {

        @Override
        public boolean wasSaved() {
          return saved;
        }

        @Override
        public DocumentSaveConfiguration getConfiguration() {
          return handlerConfig;
        }
      };
      // Invoke handlers after save operation
      for ( final NotesEventHandler handler : handlers ) {
        handler.afterSave( handlerContext, handlerResult );
      }
    } catch ( final NotesException e ) {
      throw new DataAccessException( e );
    } catch ( final ConfigurationException e ) {
      throw new DataAccessException( e );
    }
  }

  @Override
  public void delete() throws DataAccessException {
    try {
      final Document document = getSource();
      final NotesEventHandler.NotesEventHandlerContext<Document> handlerContext = new NotesEventHandler.NotesEventHandlerContext<Document>() {
        @Override
        public Document getSource() throws NotesException {
          return document;
        }

        @Override
        public Database getDatabase() throws NotesException {
          return document.getParentDatabase();
        }
      };
      final NotesEventHandler.DocumentDeleteConfiguration handlerConfig = new NotesEventHandler.DocumentDeleteConfiguration();
      handlerConfig.setSoft( NotesDriverConfiguration.DELETE_SOFT );
      handlerConfig.setForce( NotesDriverConfiguration.DELETE_FORCE );
      handlerConfig.setIgnoreFailure( false );
      final NotesEventHandler[] handlers = notesEventHandlerProvider.getNotesEventHandlers();
      // Invoke handlers before save operation
      for ( final NotesEventHandler handler : handlers ) {
        handler.beforeDelete( handlerContext, handlerConfig );
      }
      final boolean result;
      if ( handlerConfig.isSoft() ) {
        result = document.remove( handlerConfig.isForce() );
      } else {
        result = document.removePermanently( handlerConfig.isForce() );
      }
      if ( !result && !handlerConfig.isIgnoreFailure() ) {
        throw new DataAccessException( DocumentDatabaseEntry.bundle.get(
                NotesDriverConfiguration.DELETE_SOFT ? "error.delete.soft" : "error.delete.hard",
                NotesDriverConfiguration.CONFIG_FILE ) );
      }
      // Invoke handlers before save operation
      final NotesEventHandler.DocumentDeleteResult handlerResult = new NotesEventHandler.DocumentDeleteResult() {

        @Override
        public boolean wasDeleted() {
          return result;
        }

        @Override
        public DocumentDeleteConfiguration getConfiguration() {
          return handlerConfig;
        }
      };
      for ( final NotesEventHandler handler : handlers ) {
        handler.afterDelete( handlerContext, handlerResult );
      }
    } catch ( final NotesException e ) {
      throw new DataAccessException( e );
    } catch ( final ConfigurationException e ) {
      throw new DataAccessException( e );
    }
  }
}
