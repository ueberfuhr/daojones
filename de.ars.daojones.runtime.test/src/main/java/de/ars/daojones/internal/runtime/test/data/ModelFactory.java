package de.ars.daojones.internal.runtime.test.data;

import java.io.IOException;
import java.io.InputStream;

import de.ars.daojones.runtime.test.data.Model;

/**
 * A factory reading the model from an input stream.
 * 
 * @author ueberfuhr, ARS Computer und Consulting GmbH
 */
public abstract class ModelFactory {

  private ModelFactory() {
    super();
  }

  /**
   * Reads the test model from an input stream.
   * 
   * @param in
   *          the input stream
   * @return the model
   * @throws IOException
   */
  public static Model readModel( final InputStream in ) throws IOException {
    return JAXBUtilities.read( in, Model.class );
  }
}
