package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;

@DataSource
public class BusinessProcess {

  @Field(value = "comments", updatePolicy = UpdatePolicy.APPEND)
  private String comment;

}
