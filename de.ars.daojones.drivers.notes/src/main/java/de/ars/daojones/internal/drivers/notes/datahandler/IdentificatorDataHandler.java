package de.ars.daojones.internal.drivers.notes.datahandler;

import java.io.IOException;
import java.io.Serializable;

import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.View;
import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.drivers.notes.DatabaseIdentificator;
import de.ars.daojones.drivers.notes.DocumentIdentificator;
import de.ars.daojones.drivers.notes.NotesElement;
import de.ars.daojones.drivers.notes.NotesIdentificator;
import de.ars.daojones.drivers.notes.ViewEntryIdentificator;
import de.ars.daojones.internal.drivers.notes.Identificators;
import de.ars.daojones.runtime.beans.fields.Properties;
import de.ars.daojones.runtime.beans.identification.ApplicationDependentIdentificator;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class IdentificatorDataHandler extends
        InternalAbstractDataHandler<Identificator, Object /*String or identificator*/> {

  @Override
  public Class<? extends Identificator> getKeyType() {
    return Identificator.class;
  }

  @Override
  public Identificator convertAfterRead( final DataHandlerContext<?> context,
          final FieldContext<Identificator> fieldContext, final Object value ) throws NotesException,
          DataHandlerException {
    Identificator result = null;
    if ( null != value ) {
      if ( value instanceof Identificator ) {
        result = ( Identificator ) value;
      } else {
        result = NotesIdentificator.valueOf( value.toString() );
        if ( null == result ) {
          final DatabaseIdentificator db = Identificators.createIdentificator( context.getDatabase() );
          result = new DocumentIdentificator( value.toString(), db );
        }
      }
      if ( !( result instanceof ApplicationDependentIdentificator ) ) {
        final String application = context.getApplication();
        final ApplicationDependentIdentificator adi = new ApplicationDependentIdentificator();
        adi.set( application, result );
        result = adi;
      }
    }
    return result;
  }

  @Override
  public Object convertForUpdate( final DataHandlerContext<?> context, final FieldContext<Identificator> fieldContext,
          final Identificator value ) throws NotesException, DataHandlerException {
    if ( null == value
            || ( value instanceof NotesIdentificator && NotesDriverConfiguration.MODEL_PROPERTY_RICHTEXT
                    .equals( Properties.getFieldType( fieldContext.getMetadata() ) ) ) ) {
      // no conversion for rich text fields -> doc links
      return value;
    } else {
      final String application = context.getApplication();
      final Serializable id = value.getId( application );
      return null != id ? id.toString() : null;
      // TODO update last modified?!
    }
  }

  @Override
  protected void appendToRichText( final DataHandlerContext<Document> context,
          final FieldContext<Identificator> fieldContext, final Object value, final RichTextItem item )
          throws NotesException, IOException {
    appendToRichText( context, ( NotesIdentificator ) value, item );
  }

  protected void appendToRichText( final DataHandlerContext<Document> context, final NotesIdentificator value,
          final RichTextItem item ) throws NotesException, IOException {
    final NotesElement elementType = value.getElementType();
    if ( elementType == NotesElement.VIEW_ENTRY ) {
      appendToRichText( context, ( ( ViewEntryIdentificator ) value ).getView(), item );
    } else {
      final Session session = context.getSession();
      final Database db = context.getSource().getParentDatabase();
      final Base base = Identificators.findReference( session, db, value );
      switch ( elementType ) {
      case DATABASE:
        item.appendDocLink( ( Database ) base );
        break;
      case VIEW:
        item.appendDocLink( ( View ) base );
        break;
      case DOCUMENT:
        item.appendDocLink( ( Document ) base );
        break;
      default:
        throw new IllegalArgumentException( "" + elementType );
      }
    }
  }

  @Override
  protected Object readFromRichText(
          final de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext<Document> context,
          final FieldContext<Identificator> fieldContext, final RichTextItem item ) throws NotesException, IOException {
    throw new UnsupportedOperationException( "Not yet implemented!" );
  }
}
