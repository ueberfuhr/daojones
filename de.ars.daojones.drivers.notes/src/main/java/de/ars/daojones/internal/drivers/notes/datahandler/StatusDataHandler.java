package de.ars.daojones.internal.drivers.notes.datahandler;

import java.util.Date;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.drivers.notes.types.Status;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class StatusDataHandler extends InternalAbstractDataHandler<Status, Void> {

  private static final class StatusImpl implements Status {

    private static final long serialVersionUID = 1L;

    private final Date creation;
    private final Date lastModified;

    private StatusImpl( final Date creation, final Date lastModified ) {
      super();
      this.creation = creation;
      this.lastModified = lastModified;
    }

    @Override
    public Date getCreation() {
      return creation;
    }

    @Override
    public Date getLastModified() {
      return lastModified;
    }

  }

  @Override
  public Class<? extends Status> getKeyType() {
    return Status.class;
  }

  protected Date convertDate( final DateTime dt ) throws NotesException {
    return null != dt ? dt.toJavaDate() : null;
  }

  @Override
  public Status readDocument( final DataHandlerContext<Document> context, final FieldContext<Status> fieldContext )
          throws NotesException, DataHandlerException {
    final Document doc = context.getSource();
    final Date creation = convertDate( doc.getCreated() );
    final Date lastModified = convertDate( doc.getLastModified() );
    return new StatusImpl( creation, lastModified );
  }

  @Override
  public void writeDocument( final de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext<Document> context,
          final FieldContext<Status> fieldContext, final Status value ) throws NotesException, DataHandlerException {
    // nothing to do
  }

  @Override
  public Status convertAfterRead( final de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext<?> context,
          final FieldContext<Status> fieldContext, final Void value ) throws NotesException, DataHandlerException {
    // nothing to do
    return null;
  }

}
