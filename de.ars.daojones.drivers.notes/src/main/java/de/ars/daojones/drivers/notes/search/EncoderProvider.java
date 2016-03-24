package de.ars.daojones.drivers.notes.search;

/**
 * An object that provides encoders.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface EncoderProvider {

  /**
   * Returns an object that encodes values of literals.
   * 
   * @param type
   *          the class of the literal
   * @param <E>
   *          the type of the literal
   * @return the encoder
   * @throws QueryLanguageException
   *           if there isn't any encoder for the given type
   */
  <E> Encoder<E> getEncoder( Class<E> type ) throws QueryLanguageException;

}
