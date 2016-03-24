package de.ars.daojones.internal.drivers.notes.search.encoders;

import de.ars.daojones.drivers.notes.search.QueryLanguageException;

public class CharacterEncoder extends AbstractEncoder<Character> {

  @Override
  public Class<? extends Character> getKeyType() {
    return Character.class;
  }

  @Override
  protected String getKey( final EncoderContext<Character> context ) throws QueryLanguageException {
    return "string.literal";
  }

}
