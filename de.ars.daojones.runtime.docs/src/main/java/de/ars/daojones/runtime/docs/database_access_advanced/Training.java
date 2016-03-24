package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.identification.IdentifiedBy;

@DataSource("Training")
@IdentifiedBy(CustomBeanIdentificator.class)
public class Training {
  // ...
}
