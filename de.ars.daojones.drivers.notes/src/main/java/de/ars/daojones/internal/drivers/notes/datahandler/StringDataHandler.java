package de.ars.daojones.internal.drivers.notes.datahandler;

import java.io.IOException;

import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;

public class StringDataHandler extends InternalAbstractDataHandler<String, String> {

  @Override
  public Class<? extends String> getKeyType() {
    return String.class;
  }

  @Override
  public String convertAfterRead( final DataHandlerContext<?> context, final FieldContext<String> fieldContext,
          final String value ) throws NotesException, DataHandlerException {
    return value;
  }

  @Override
  protected Object readDocumentItem( final DataHandlerContext<Document> context,
          final FieldContext<String> fieldContext ) throws NotesException, IOException {
    final boolean isRichText = isRichText( context, fieldContext );
    final Document doc = context.getSource();
    final Item item = doc.getFirstItem( fieldContext.getName() );
    if ( isRichText && null != item && Item.RICHTEXT == item.getType() ) {
      return readFromRichText( context, fieldContext, ( RichTextItem ) item );
    } else {
      return doc.getItemValueString( fieldContext.getName() );
    }
  }

  @Override
  protected String getValueToConvert( final Object item ) {
    return null != item ? item.toString() : null;
  }

}
