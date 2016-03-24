package de.ars.daojones.runtime.docs.drivers.notes;

import de.ars.daojones.drivers.notes.DatabaseIdentificator;
import de.ars.daojones.drivers.notes.ViewIdentificator;
import de.ars.daojones.drivers.notes.annotations.RichText;
import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.beans.identification.Identificator;

@DataSource
public class DocLinkExample {

  @RichText
  @Field("views")
  public Identificator createDocumentLink() {
    final String viewName = "All documents";
    final String replicaId = "C17832332";
    final String server = null;
    return new ViewIdentificator(viewName, new DatabaseIdentificator(replicaId, server));
  }

}
