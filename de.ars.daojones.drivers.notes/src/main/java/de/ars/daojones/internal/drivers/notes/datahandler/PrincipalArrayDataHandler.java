package de.ars.daojones.internal.drivers.notes.datahandler;

import java.io.IOException;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.drivers.notes.types.Principal;
import de.ars.daojones.runtime.beans.fields.Properties;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class PrincipalArrayDataHandler extends AbstractArrayDataHandler<Principal> {

  @Override
  public Class<? extends Principal[]> getKeyType() {
    return Principal[].class;
  }

  @Override
  protected Item writeDocumentItem( final DataHandlerContext<Document> context,
          final FieldContext<Object> fieldContext, final Object value ) throws NotesException, IOException {
    // if value is null, create the item
    final Item item = super.writeDocumentItem( context, fieldContext, value );
    if ( null != item ) {
      item.setNames( true );
      final String fieldType = Properties.getFieldType( fieldContext.getMetadata() );
      if ( NotesDriverConfiguration.MODEL_PROPERTY_READERS.equals( fieldType ) ) {
        item.setReaders( true );
      } else if ( NotesDriverConfiguration.MODEL_PROPERTY_AUTHORS.equals( fieldType ) ) {
        item.setAuthors( true );
      }
    }
    return item;
  }

}
