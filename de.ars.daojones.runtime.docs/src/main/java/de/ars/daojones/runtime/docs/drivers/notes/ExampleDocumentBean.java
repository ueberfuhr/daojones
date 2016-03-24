package de.ars.daojones.runtime.docs.drivers.notes;

import java.util.Date;

import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;

@DataSource(value = "ExampleForm", type = DataSourceType.TABLE)
public class ExampleDocumentBean {

  @Field
  private String title;

  @Field
  private Date date;

  // ...

}
