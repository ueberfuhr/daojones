package de.ars.daojones.internal.runtime.test.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class StreamHelper {

  private StreamHelper() {
    super();
  }

  public static <T extends OutputStream> T inOut( final InputStream in, final T out, final int bufferSize,
          final boolean close ) throws IOException {
    try {
      final byte[] buf = new byte[bufferSize];
      if ( null != in ) {
        int len = in.read( buf );
        while ( len > 0 ) {
          out.write( buf, 0, len );
          len = in.read( buf );
        }
      }
      return out;
    } finally {
      try {
        out.close();
      } finally {
        if ( null != in ) {
          in.close();
        }
      }
    }
  }
}
