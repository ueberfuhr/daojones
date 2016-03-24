package de.ars.daojones.internal.drivers.notes.datahandler;

import lotus.domino.Document;
import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class CharacterDataHandler extends InternalAbstractDataHandler<Character, String> {

  @Override
  public Class<? extends Character> getKeyType() {
    return Character.class;
  }

  @Override
  public Character convertAfterRead( final DataHandlerContext<?> context, final FieldContext<Character> fieldContext,
          final String value ) throws NotesException, DataHandlerException {
    return null != value && value.length() > 0 ? value.charAt( 0 ) : null;
  }

  @Override
  protected Object readDocumentItem( final DataHandlerContext<Document> context,
          final FieldContext<Character> fieldContext ) throws NotesException {
    return context.getSource().getItemValueString( fieldContext.getName() );
  }

  @Override
  protected String getValueToConvert( final Object item ) {
    return null != item ? item.toString() : null;
  }

  @Override
  public String convertForUpdate( final DataHandlerContext<?> context, final FieldContext<Character> fieldContext,
          final Character value ) throws NotesException, DataHandlerException {
    return String.valueOf( value );
  }

}
