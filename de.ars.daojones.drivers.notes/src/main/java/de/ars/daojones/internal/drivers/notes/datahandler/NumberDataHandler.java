package de.ars.daojones.internal.drivers.notes.datahandler;

import lotus.domino.Document;
import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class NumberDataHandler extends InternalAbstractDataHandler<Number, Number> {

  @Override
  public Class<? extends Number> getKeyType() {
    return Number.class;
  }

  @Override
  protected Object readDocumentItem( final DataHandlerContext<Document> context, final FieldContext<Number> fieldContext )
          throws NotesException {
    final Class<? extends Number> type = fieldContext.getType();
    final String field = fieldContext.getName();
    if ( type.isAssignableFrom( Float.class ) || type.isAssignableFrom( Float.TYPE )
            || type.isAssignableFrom( Double.class ) || type.isAssignableFrom( Double.TYPE ) ) {
      return context.getSource().getItemValueDouble( field );
    } else {
      return context.getSource().getItemValueInteger( field );
    }
  }

  @Override
  protected Number getValueToConvert( final Object item ) {
    return ( Number ) item;
  }

  @Override
  public Number convertAfterRead( final DataHandlerContext<?> context, final FieldContext<Number> fieldContext,
          final Number value ) throws NotesException, DataHandlerException {
    final Class<? extends Number> type = fieldContext.getType();
    // value could be of type double in case of arrays -> read out as Vector<Double>
    if ( null == value ) {
      return null;
    } else if ( type.isAssignableFrom( Double.class ) || type.isAssignableFrom( Double.TYPE ) ) {
      return value.doubleValue();
    } else if ( type.isAssignableFrom( Float.class ) || type.isAssignableFrom( Float.TYPE ) ) {
      return value.floatValue();
    } else if ( type.isAssignableFrom( Integer.class ) || type.isAssignableFrom( Integer.TYPE ) ) {
      return value.intValue();
    } else if ( type.isAssignableFrom( Long.class ) || type.isAssignableFrom( Long.TYPE ) ) {
      return value.longValue();
    } else if ( type.isAssignableFrom( Short.class ) || type.isAssignableFrom( Short.TYPE ) ) {
      return value.shortValue();
    } else if ( type.isAssignableFrom( Byte.class ) || type.isAssignableFrom( Byte.TYPE ) ) {
      return value.byteValue();
    }
    // should not occur
    throw new DataHandlerException( this );
  }
}
