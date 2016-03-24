package de.ars.daojones.runtime.docs.database_access_basics;

import java.util.Date;

import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.beans.annotations.FieldRef;

public class Person {

  // field mapping to a field with a same name
  @Field
  private String firstName;
  // field mapping to a field with a different name
  @Field("familyName")
  private String lastName;

  private Gender gender;

  // read-only field mapping
  public Person(@Field final Date birthDate) {
    super();
    // ...
  }

  // read-only field mapping
  public void setSize(@Field final double size) {
    // ...
  }

  // read-only field mappings - multiple per method
  public void initialize(@Field final double size, @Field final HairColor hairColor) {
    // ...
  }

  // field mapping over methods - mapping
  public void setGender(@Field(id = "gender-field") final Gender gender) {
    this.gender = gender;
  }

  // field mapping over methods - reference
  @FieldRef("gender-field")
  public Gender getGender() {
    return gender;
  }

}
