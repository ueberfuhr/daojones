package de.ars.daojones.runtime.docs.test_support;

import de.ars.daojones.runtime.beans.annotations.Id;
import de.ars.daojones.runtime.beans.identification.Identificator;

public class Memo {

  @Id
  private Identificator id;

  public Identificator getId() {
    return id;
  }

}
