package de.ars.daojones.internal.runtime.test.datahandler;

import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.test.spi.database.DataHandler;
import de.ars.daojones.runtime.test.spi.database.TestModelIndex;

public class NumberDataHandler implements DataHandler<Number> {

  @Override
  public Class<? extends Number> getKeyType() {
    return Number.class;
  }

  @Override
  public Number convertRead( final FieldContext<Number> context, final TestModelIndex index, final String value )
          throws DataAccessException, UnsupportedFieldTypeException {
    try {
      final Class<? extends Number> type = context.getType();
      if ( null == value ) {
        return null;
      } else if ( type.isAssignableFrom( Double.class ) || type.isAssignableFrom( Double.TYPE ) ) {
        return Double.valueOf( value );
      } else if ( type.isAssignableFrom( Float.class ) || type.isAssignableFrom( Float.TYPE ) ) {
        return Float.valueOf( value );
      } else if ( type.isAssignableFrom( Integer.class ) || type.isAssignableFrom( Integer.TYPE ) ) {
        return Integer.valueOf( value );
      } else if ( type.isAssignableFrom( Long.class ) || type.isAssignableFrom( Long.TYPE ) ) {
        return Long.valueOf( value );
      } else if ( type.isAssignableFrom( Short.class ) || type.isAssignableFrom( Short.TYPE ) ) {
        return Short.valueOf( value );
      } else if ( type.isAssignableFrom( Byte.class ) || type.isAssignableFrom( Byte.TYPE ) ) {
        return Byte.valueOf( value );
      }
      // should not occur
      throw new DataAccessException();
    } catch ( final NumberFormatException e ) {
      throw new DataAccessException( e );
    }
  }

  @Override
  public String convertWrite( final FieldContext<Number> context, final TestModelIndex index, final Number value )
          throws DataAccessException, UnsupportedFieldTypeException {
    try {
      final Class<? extends Number> type = context.getType();
      if ( null == value ) {
        return null;
      } else if ( type.isAssignableFrom( Double.class ) || type.isAssignableFrom( Double.TYPE ) ) {
        return Double.toString( value.doubleValue() );
      } else if ( type.isAssignableFrom( Float.class ) || type.isAssignableFrom( Float.TYPE ) ) {
        return Float.toString( value.floatValue() );
      } else if ( type.isAssignableFrom( Integer.class ) || type.isAssignableFrom( Integer.TYPE ) ) {
        return Integer.toString( value.intValue() );
      } else if ( type.isAssignableFrom( Long.class ) || type.isAssignableFrom( Long.TYPE ) ) {
        return Long.toString( value.longValue() );
      } else if ( type.isAssignableFrom( Short.class ) || type.isAssignableFrom( Short.TYPE ) ) {
        return Short.toString( value.shortValue() );
      } else if ( type.isAssignableFrom( Byte.class ) || type.isAssignableFrom( Byte.TYPE ) ) {
        return Byte.toString( value.byteValue() );
      }
      // should not occur
      throw new DataAccessException();
    } catch ( final NumberFormatException e ) {
      throw new DataAccessException( e );
    }
  }

}
