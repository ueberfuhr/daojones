package de.ars.daojones.internal.drivers.notes.datahandler;

import de.ars.daojones.runtime.beans.identification.Identificator;

public class IdentificatorArrayDataHandler extends AbstractArrayDataHandler<Identificator> {

  @Override
  public Class<? extends Identificator[]> getKeyType() {
    return Identificator[].class;
  }

}
