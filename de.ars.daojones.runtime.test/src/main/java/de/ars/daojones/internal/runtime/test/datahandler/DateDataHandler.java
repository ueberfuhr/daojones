package de.ars.daojones.internal.runtime.test.datahandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.test.spi.database.DataHandler;
import de.ars.daojones.runtime.test.spi.database.TestModelIndex;

public class DateDataHandler implements DataHandler<Date> {

  private final DateFormat customFormat = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss.SSSS" );
  private final DateFormat[] allowedFormats;

  {
    final int[] styles = { DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT };
    final Locale[] locales = { Locale.GERMAN, Locale.ENGLISH, Locale.getDefault() };
    final Collection<DateFormat> formats = new LinkedList<DateFormat>();
    // custom format first
    formats.add( customFormat );
    for ( final int datestyle : styles ) {
      for ( final int timestyle : styles ) {
        for ( final Locale locale : locales ) {
          formats.add( DateFormat.getDateTimeInstance( datestyle, timestyle, locale ) );
        }
      }
    }
    allowedFormats = formats.toArray( new DateFormat[formats.size()] );
  }

  @Override
  public Class<? extends Date> getKeyType() {
    return Date.class;
  }

  @Override
  public Date convertRead( final FieldContext<Date> context, final TestModelIndex index, final String value )
          throws DataAccessException, UnsupportedFieldTypeException {
    if ( null == value ) {
      return null;
    } else {
      ParseException e = null;
      for ( final DateFormat format : allowedFormats ) {
        try {
          final Date result = format.parse( value );
          return result;
        } catch ( final ParseException pe ) {
          if ( null == e ) {
            e = pe;
          }
        }
      }
      // Should never occur
      throw new DataAccessException( value, e );
    }
  }

  @Override
  public String convertWrite( final FieldContext<Date> context, final TestModelIndex index, final Date value )
          throws DataAccessException, UnsupportedFieldTypeException {
    return null != value ? customFormat.format( value ) : null;
  }

}
