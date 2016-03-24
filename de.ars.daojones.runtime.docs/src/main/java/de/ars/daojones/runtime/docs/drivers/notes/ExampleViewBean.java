package de.ars.daojones.runtime.docs.drivers.notes;

import de.ars.daojones.drivers.notes.annotations.ViewColumn.DocumentMapped;
import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;

@DataSource(value = "ExampleView", type = DataSourceType.VIEW)
public class ExampleViewBean {

  // field to search for
  @Field(id = "title", value = "?1")
  private String title;

  @DocumentMapped
  @Field
  private ExampleDocumentBean exampleDocumentBean;

  // ...

}
