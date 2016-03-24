package de.ars.daojones.runtime.docs.database_access_basics;

import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Id;

@DataSource("Training")
public class Training {
  @Id
  private Object id;
  // ...
}
