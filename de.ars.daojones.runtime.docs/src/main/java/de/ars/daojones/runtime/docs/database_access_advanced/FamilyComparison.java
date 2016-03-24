package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.query.Comparison;

public class FamilyComparison implements Comparison<Person> {

  @Override
  public boolean matches(final Person left, final Person right) {
    // if they have the same last name, they belong to the same family
    return left.getLastName().equals(right.getLastName());
  }

  @Override
  public Class<Person> getType() {
    return Person.class;
  }

}
