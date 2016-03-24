package de.ars.daojones.internal.runtime.test.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class SerializationHelper {

  private SerializationHelper() {
    super();
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

  public static Serializable deserialize( final byte[] arr ) throws IOException, ClassNotFoundException {
    if ( null != arr ) {
      final ByteArrayInputStream bai = new ByteArrayInputStream( arr );
      try {
        final ObjectInputStream ois = new ObjectInputStream( bai );
        try {
          final Serializable result = ( Serializable ) ois.readObject();
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
