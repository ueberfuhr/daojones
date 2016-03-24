package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.beans.annotations.Metadata;
import de.ars.daojones.runtime.beans.annotations.Property;

@DataSource
public class PropertyExample {

  @Property(name = "myProperty", value = "myValue")
  @Field
  private String field1;

  @Metadata({ @Property(name = "myProperty1", value = "myValue1"),
      @Property(name = "myProperty2", value = "myValue2") })
  @Field
  private String field2;

}
