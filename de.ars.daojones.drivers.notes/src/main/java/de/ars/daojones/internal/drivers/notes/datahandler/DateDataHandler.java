package de.ars.daojones.internal.drivers.notes.datahandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.runtime.beans.fields.Properties;
import de.ars.daojones.runtime.configuration.beans.Property;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import lotus.domino.DateTime;
import lotus.domino.NotesException;

public class DateDataHandler extends InternalAbstractDataHandler<Date, Object> {

  @Override
  public Class<? extends Date> getKeyType() {
    return Date.class;
  }

  protected Date toDate( final String field, final Object datetime, final List<Property> metadata )
          throws NotesException, DataHandlerException {
    if ( null == datetime ) {
      return null;
    } else if ( datetime instanceof Date ) {
      return ( Date ) datetime;
    } else if ( datetime instanceof DateTime ) {
      final DateTime dt = ( DateTime ) datetime;
      final Date result = dt.toJavaDate();
      final Property fieldType = Properties.getProperty( metadata, NotesDriverConfiguration.MODEL_PROPERTY_FIELD_TYPE );
      if ( null != fieldType && fieldType.getValue().equals( NotesDriverConfiguration.MODEL_PROPERTY_LOCALTIME ) ) {
        // read as local time
        try {
          final Calendar cal = Calendar.getInstance();
          cal.setTime( result );
          final String localTime = dt.getLocalTime(); // hh:mm:ss
          final String[] parts = localTime.split( ":" ); // should be array of length 3
          cal.set( Calendar.HOUR, Integer.parseInt( parts[0] ) );
          if ( parts.length > 1 ) {
            cal.set( Calendar.MINUTE, Integer.parseInt( parts[1] ) );
          }
          if ( parts.length > 2 ) {
            cal.set( Calendar.SECOND, Integer.parseInt( parts[2] ) );
          }
          if ( parts.length > 3 ) {
            cal.set( Calendar.MILLISECOND, Integer.parseInt( parts[3] ) );
          }
          result.setTime( cal.getTimeInMillis() );
        } catch ( final Exception e ) {
          throw new DataHandlerException( this, e );
        }
      }
      return result;
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
    return toDate( fieldContext.getName(), value, fieldContext.getMetadata() );
  }

  @Override
  public Object convertForUpdate( final DataHandlerContext<?> context, final FieldContext<Date> fieldContext,
          final Date value ) throws DataHandlerException, NotesException {
    return null != value ? context.getSession().createDateTime( value ) : null;
  }

}
