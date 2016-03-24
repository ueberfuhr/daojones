package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;

@DataSource(value = "Authors", type = DataSourceType.VIEW)
public class Author {

  @Field("?0")
  private String surname;
  @Field("?1")
  private String givenname;

}
