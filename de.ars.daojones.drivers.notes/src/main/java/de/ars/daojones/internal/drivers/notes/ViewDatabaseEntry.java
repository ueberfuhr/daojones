package de.ars.daojones.internal.drivers.notes;

import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.drivers.notes.DataHandler;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.drivers.notes.DataHandlerProvider;
import de.ars.daojones.internal.drivers.notes.DocumentDatabaseEntry.NotesEventHandlerProvider;
import de.ars.daojones.runtime.beans.fields.Properties;
import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.spi.database.ViewHelper;

public class ViewDatabaseEntry extends AbstractBaseEntry<ViewEntry> {

  private final View view;
  private final DocumentDatabaseEntry documentEntry;

  public ViewDatabaseEntry( final View view, final ViewEntry entry, final BeanModel beanModel,
          final DataHandlerProvider dataHandlerProvider, final NotesEventHandlerProvider notesEventHandlerProvider )
          throws NotesException {
    super( entry, beanModel, dataHandlerProvider );
    this.view = view;
    documentEntry = new DocumentDatabaseEntry( entry.getDocument(), beanModel, dataHandlerProvider,
            notesEventHandlerProvider );
  }

  /**
   * Returns the view.
   * 
   * @return the view
   */
  protected View getView() {
    return view;
  }

  /**
   * Decides whether a field is mapped to a column value or to the document
   * behind the view entry.
   * 
   * @param fieldContext
   *          the field context
   * @return <tt>true</tt> if a rich text item is used for the context
   */
  protected boolean isDocumentMapped( final FieldContext<?> fieldContext ) {
    return NotesDriverConfiguration.MODEL_PROPERTY_DOCUMENT_MAPPED.equals( Properties.getFieldType( fieldContext
            .getMetadata() ) );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public <E> E getFieldValue( final FieldContext<E> context ) throws DataAccessException, UnsupportedFieldTypeException {
    if ( isDocumentMapped( context ) && DatabaseAccessor.class.getName().equals( context.getType().getName() ) ) {
      return ( E ) documentEntry;
    } else {
      return super.getFieldValue( context );
    }

  }

  @Override
  protected <E> E getFieldValue( final FieldContext<E> context, final DataHandler<E> handler )
          throws DataAccessException, NotesException, DataHandlerException {
    return handler.readView( this, context );
  }

  @Override
  protected <E> void setFieldValue( final FieldContext<E> context, final DataHandler<E> handler, final E value )
          throws DataAccessException, NotesException, DataHandlerException {
    handler.writeView( this, context, value );
  }

  @Override
  public Database getDatabase() throws NotesException {
    return documentEntry.getDatabase();
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public String[] getFields() throws DataAccessException {
    try {
      final Vector<ViewColumn> columns = getView().getColumns();
      final Set<String> fields = new TreeSet<String>(); // nice to have columns sorted by name
      int idx = 0;
      for ( final ViewColumn column : columns ) {
        fields.add( column.getTitle() );
        // column indexes
        fields.add( ViewHelper.toColumnName( idx ) );
        // programmatic item name
        fields.add( column.getItemName() );
        idx++;
      }
      fields.remove( null );
      fields.remove( "" );
      return fields.toArray( new String[fields.size()] );
    } catch ( final NotesException e ) {
      throw new DataAccessException( e );
    }
  }

  @Override
  public Identificator getIdentificator() throws DataAccessException {
    try {
      return Identificators.createIdentificator( view, getSource() );
    } catch ( final NotesException e ) {
      throw new DataAccessException( e );
    }
  }

  @Override
  public void store() throws DataAccessException {
    documentEntry.store();
  }

  @Override
  public void delete() throws DataAccessException {
    documentEntry.delete();
  }

}
