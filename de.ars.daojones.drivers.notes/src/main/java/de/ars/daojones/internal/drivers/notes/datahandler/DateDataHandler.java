package de.ars.daojones.internal.drivers.notes.datahandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class DateDataHandler extends InternalAbstractDataHandler<Date, Object> {

  @Override
  public Class<? extends Date> getKeyType() {
    return Date.class;
  }

  protected Date toDate( final String field, final Object datetime ) throws NotesException, DataHandlerException {
    if ( null == datetime ) {
      return null;
    } else if ( datetime instanceof Date ) {
      return ( Date ) datetime;
    } else if ( datetime instanceof lotus.domino.cso.DateTime ) {
      return ( ( lotus.domino.cso.DateTime ) datetime ).toJavaDate();
    } else if ( datetime instanceof lotus.domino.local.DateTime ) {
      return ( ( lotus.domino.local.DateTime ) datetime ).toJavaDate();
    } else if ( datetime instanceof String ) {
      logMessage( Level.WARNING, "datetime.stringformat", field, datetime );
      final int[] styles = { DateFormat.SHORT, DateFormat.MEDIUM, DateFormat.LONG, DateFormat.FULL };
      final Locale[] locales = { Locale.GERMAN, Locale.ENGLISH, Locale.getDefault() };
      for ( final int style : styles ) {
        for ( final Locale locale : locales ) {
          try {
            final Date result = DateFormat.getDateInstance( style, locale ).parse( ( String ) datetime );
            return result;
          } catch ( final ParseException e ) {
            throw new DataHandlerException( this, e );
          }
        }
      }
      // Should never occur
      throw new DataHandlerException( this );
    } else {
      throw new DataHandlerException( this, getMessage( "datetime.invalidformat", field, datetime ) );
    }
  }

  @Override
  public Date convertAfterRead( final DataHandlerContext<?> context, final FieldContext<Date> fieldContext,
          final Object value ) throws NotesException, DataHandlerException {
    return toDate( fieldContext.getName(), value );
  }

  @Override
  public Object convertForUpdate( final DataHandlerContext<?> context, final FieldContext<Date> fieldContext,
          final Date value ) throws DataHandlerException, NotesException {
    return null != value ? context.getSession().createDateTime( value ) : null;
  }

}
