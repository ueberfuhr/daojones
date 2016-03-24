package de.ars.daojones.internal.drivers.notes.search.encoders;

import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.drivers.notes.types.Principal;

public class PrincipalEncoder extends AbstractEncoder<Principal> {

  @Override
  public Class<? extends Principal> getKeyType() {
    return Principal.class;
  }

  @Override
  protected String getKey( final de.ars.daojones.drivers.notes.search.Encoder.EncoderContext<Principal> context )
          throws QueryLanguageException {
    return "principal.literal";
  }

}
