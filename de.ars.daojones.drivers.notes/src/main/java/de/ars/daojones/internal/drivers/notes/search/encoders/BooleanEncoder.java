package de.ars.daojones.internal.drivers.notes.search.encoders;

import de.ars.daojones.drivers.notes.search.EncoderProvider;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;

public class BooleanEncoder extends AbstractEncoder<Boolean> {

  @Override
  public Class<? extends Boolean> getKeyType() {
    return Boolean.class;
  }

  @Override
  public String encodeLiteral( final EncoderContext<Boolean> context, final EncoderProvider provider,
          final Boolean value ) throws QueryLanguageException {
    return getTemplate( context, "boolean.format." + Boolean.toString( value ) );
  }

  @Override
  protected String getKey( final EncoderContext<Boolean> context ) throws QueryLanguageException {
    return null;
  }

}
