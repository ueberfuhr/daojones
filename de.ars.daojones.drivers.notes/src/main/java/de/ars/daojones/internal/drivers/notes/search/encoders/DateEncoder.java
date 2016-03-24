package de.ars.daojones.internal.drivers.notes.search.encoders;

import java.util.Date;

import de.ars.daojones.drivers.notes.search.QueryLanguageException;

public class DateEncoder extends AbstractEncoder<Date> {

  @Override
  public Class<? extends Date> getKeyType() {
    return Date.class;
  }

  @Override
  protected String getKey( final de.ars.daojones.drivers.notes.search.Encoder.EncoderContext<Date> context )
          throws QueryLanguageException {
    return "datetime.literal.time";
  }

}
