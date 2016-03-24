package de.ars.daojones.internal.cache.ws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.ars.daojones.runtime.spi.cache.CacheContext;

final class CacheHelper {

  private CacheHelper() {
    super();
  }

  public static String getProperty( final CacheContext<?, ?> context, final String key, final String defaultValue ) {
    final String result = context.getProperties().getProperty( key );
    return null != result ? result : defaultValue;
  }

  public static byte[] serialize( final Object o ) throws IOException {
    if ( null != o ) {
      final ByteArrayOutputStream bao = new ByteArrayOutputStream();
      try {
        final ObjectOutputStream oos = new ObjectOutputStream( bao );
        try {
          oos.writeObject( o );
          final byte[] result = bao.toByteArray();
          return result;
        } finally {
          oos.close();
        }
      } finally {
        bao.close();
      }
    } else {
      return null;
    }
  }

  public static Object deserialize( final byte[] arr ) throws IOException, ClassNotFoundException {
    if ( null != arr ) {
      final ByteArrayInputStream bai = new ByteArrayInputStream( arr );
      try {
        final ObjectInputStream ois = new ObjectInputStream( bai );
        try {
          final Object result = ois.readObject();
          return result;
        } finally {
          ois.close();
        }
      } finally {
        bai.close();
      }
    } else {
      return null;
    }
  }

}
