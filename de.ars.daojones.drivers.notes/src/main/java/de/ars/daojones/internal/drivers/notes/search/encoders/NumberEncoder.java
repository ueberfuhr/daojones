package de.ars.daojones.internal.drivers.notes.search.encoders;

import de.ars.daojones.drivers.notes.search.QueryLanguageException;

public class NumberEncoder extends AbstractEncoder<Number> {

  @Override
  public Class<? extends Number> getKeyType() {
    return Number.class;
  }

  @Override
  protected String getKey( final de.ars.daojones.drivers.notes.search.Encoder.EncoderContext<Number> context )
          throws QueryLanguageException {
    return "number.literal";
  }

}
