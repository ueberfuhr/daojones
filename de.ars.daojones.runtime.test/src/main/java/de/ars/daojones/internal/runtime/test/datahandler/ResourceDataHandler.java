package de.ars.daojones.internal.runtime.test.datahandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;

import de.ars.daojones.internal.runtime.test.utilities.Base64Coder;
import de.ars.daojones.internal.runtime.test.utilities.StreamHelper;
import de.ars.daojones.runtime.beans.fields.ByteArrayResource;
import de.ars.daojones.runtime.beans.fields.Resource;
import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.test.spi.database.DataHandler;
import de.ars.daojones.runtime.test.spi.database.TestModelIndex;

public class ResourceDataHandler implements DataHandler<Resource> {

  private static final int DEFAULT_BUFFER_SIZE = 4096;
  private static final String DATA_URL_PREFIX = "data:";
  private static final String DATA_URL_SEPARATOR = ";base64,";

  @Override
  public Class<? extends Resource> getKeyType() {
    return Resource.class;
  }

  @Override
  public Resource convertRead( final FieldContext<Resource> context, final TestModelIndex index, final String value )
          throws DataAccessException, UnsupportedFieldTypeException {
    try {
      if ( null != value ) {
        final int initialIdx = value.indexOf( ResourceDataHandler.DATA_URL_PREFIX );
        final int idxOfContentType = initialIdx + ResourceDataHandler.DATA_URL_PREFIX.length();
        final int idxOfSeparator = value.indexOf( ResourceDataHandler.DATA_URL_SEPARATOR );
        final int idxOfBase64 = idxOfSeparator + ResourceDataHandler.DATA_URL_SEPARATOR.length();
        if ( initialIdx != 0 ) {
          throw new ParseException( value, 0 );
        } else if ( idxOfSeparator < 0 ) {
          throw new ParseException( value, value.length() - 1 );
        } else if ( idxOfBase64 >= value.length() - 1 ) {
          throw new ParseException( value, value.length() - 1 );
        } else {
          final String contentType = value.substring( idxOfContentType, idxOfSeparator );
          final byte[] content = Base64Coder.decodeLines( value.substring( idxOfBase64 ) );
          return new ByteArrayResource( content, context.getName(), contentType );
        }
      } else {
        return null;
      }
    } catch ( final ParseException e ) {
      throw new DataAccessException( e );
    }
  }

  @Override
  public String convertWrite( final FieldContext<Resource> context, final TestModelIndex index, final Resource value )
          throws DataAccessException, UnsupportedFieldTypeException {
    try {
      if ( null != value ) {
        final StringBuffer sb = new StringBuffer();
        final byte[] data = StreamHelper.inOut( value.getInputStream(), new ByteArrayOutputStream(),
                ResourceDataHandler.DEFAULT_BUFFER_SIZE, true ).toByteArray();
        sb.append( ResourceDataHandler.DATA_URL_PREFIX ).append( value.getContentType() )
                .append( ResourceDataHandler.DATA_URL_SEPARATOR ).append( Base64Coder.encodeLines( data ) );
        return sb.toString();
      } else {
        return null;
      }
    } catch ( final IOException e ) {
      throw new DataAccessException( e );
    }
  }
}
