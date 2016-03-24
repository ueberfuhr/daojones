package de.ars.daojones.internal.drivers.notes.datahandler;

import lotus.domino.Document;
import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.runtime.beans.fields.Resource;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class ResourceArrayDataHandler extends AbstractArrayDataHandler<Resource> {

  @Override
  public Class<? extends Resource[]> getKeyType() {
    return Resource[].class;
  }

  @Override
  public Object readDocument( final de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext<Document> context,
          final FieldContext<Object> fieldContext ) throws NotesException, DataHandlerException {
    // TODO Auto-generated method stub
    return super.readDocument( context, fieldContext );
  }

  @Override
  public void writeDocument( final de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext<Document> context,
          final FieldContext<Object> fieldContext, final Object value ) throws NotesException, DataHandlerException {
    // TODO Auto-generated method stub
    super.writeDocument( context, fieldContext, value );
  }

  // TODO multiple embedded objects aus RT item lesen
  // TODO Rich Text als MIME Type

}
