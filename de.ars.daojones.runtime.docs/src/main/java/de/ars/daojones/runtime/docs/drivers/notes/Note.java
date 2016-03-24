package de.ars.daojones.runtime.docs.drivers.notes;

import java.security.Principal;

import de.ars.daojones.drivers.notes.annotations.Authors;
import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;

@DataSource("Note")
public class Note {

  @Authors
  @Field("Author")
  private Principal author;

  public Principal getAuthor() {
    return author;
  }

  public void setAuthor(final Principal author) {
    this.author = author;
  }

}
