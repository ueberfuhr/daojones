package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.annotations.Computed;
import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;

@DataSource("Car")
public class Car {

  // the amount of gas
  @Field
  private int fuel;
  // litres per 100 miles
  @Field
  private double consumption;
  // how many miles can we drive with the current fuel
  @Computed
  @Field
  private double range;

}
