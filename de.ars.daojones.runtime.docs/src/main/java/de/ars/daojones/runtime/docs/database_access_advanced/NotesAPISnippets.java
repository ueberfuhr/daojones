package de.ars.daojones.runtime.docs.database_access_advanced;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import de.ars.daojones.drivers.notes.xt.ComputeWithFormHandler;
import de.ars.daojones.drivers.notes.xt.NotesAPI;
import de.ars.daojones.drivers.notes.xt.NotesEventHandler;
import de.ars.daojones.drivers.notes.xt.NotesEventHandlerAdapter;
import de.ars.daojones.drivers.notes.xt.UpdateIndexHandler;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.DataAccessException;

public class NotesAPISnippets {

  private Connection<Memo> connection;

  @SuppressWarnings("unused")
  public void example1() throws NotesException, DataAccessException,
          ConfigurationException {
    // -1-
    // get session from connection
    final Session session = NotesAPI.getSession(connection);
    final Memo memo = connection.find();
    // get document assigned to the bean
    final Document doc = NotesAPI.getDocument(connection, memo);
  }

  @SuppressWarnings("unused")
  public void example2() throws NotesException, DataAccessException,
          ConfigurationException, FieldAccessException {
    // get session from connection
    final Session session = NotesAPI.getSession(connection);
    final Memo _memo = connection.find();
    // get document assigned to the bean
    final Document doc = NotesAPI.getDocument(connection, _memo);
    // -1-
    // create bean from document
    final Memo memo = NotesAPI.createBean(connection, doc);
  }

  public void example3() {
    final Document parentDoc = null;
    // -1-
    final NotesEventHandler handler = new NotesEventHandlerAdapter() {
      @Override
      public void beforeSave(final NotesEventHandlerContext<Document> context,
              final DocumentSaveConfiguration config) throws NotesException {
        // Further exceptions allowed: DataAccessException, ConfigurationException
        // Access the document
        final Document doc = context.getSource();
        // Response document
        doc.makeResponse(parentDoc);
        // mark the document as read during save operation
        config.setMarkRead(true);
      }
    };
    // add handler to connection to register for the assigned bean type
    NotesAPI.addNotesEventHandler(connection, handler);
  }

  public void example4() {
    // -1-
    // Validate and compute fields before saving a document
    final ComputeWithFormHandler h1 = new ComputeWithFormHandler();
    // (optional) do not save "tempField" into the document
    h1.getTransientFields().add("tempField");
    // (optional) validate and compute fields with an alternate form
    h1.setAlternateForm("MemoPart");
    NotesAPI.addNotesEventHandler(connection, h1);

    // -2-
    // Update the full-text index before executing the search
    // false=do not create any index if it does not already exist (only local databases) 
    final UpdateIndexHandler h2 = new UpdateIndexHandler(false);
    NotesAPI.addNotesEventHandler(connection, h2);
  }

}
