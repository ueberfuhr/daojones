package de.ars.daojones.runtime.docs.database_access_basics;

import static de.ars.daojones.runtime.query.SearchCriterionBuilder.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.provider.AnnotationBeanConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.XmlConnectionConfigurationSource;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterion;
import de.ars.daojones.runtime.query.SearchCriterionVisitor;

@SuppressWarnings("unused")
public class CodeSnippets {

  public void createContext() throws ConfigurationException, IOException {
    // -1-
    final DaoJonesContextFactory factory = new DaoJonesContextFactory();

    // -2-
    // Configure Bean Model (scan classpath for annotations)
    final String application = "mail-application";
    final ConfigurationSource beanConfig = new AnnotationBeanConfigurationSource(
            application);
    factory.setConfigurationSources(beanConfig);

    // -3-
    // Configure Connections
    final URL connectionConfigURL = Memo.class.getResource("daojones-connections.xml");
    final ConfigurationSource connectionConfig = new XmlConnectionConfigurationSource(
            application, connectionConfigURL);
    factory.setConfigurationSources(connectionConfig);

    // -4-
    // Create DaoJones Context
    final DaoJonesContext ctx = factory.createContext();
    try {
      final Application app = ctx.getApplication(application);
      try {
        // Find connection for Memo type
        final Connection<Memo> con = app.getConnection(Memo.class);
        try {
          // Access database
        } finally {
          con.close();
        }
      } finally {
        app.close();
      }
    } finally {
      ctx.close();
    }

  }

  public void crud(final Connection<Memo> con) throws ConfigurationException,
          DataAccessException {
    // -1-
    final Memo newMemo = new Memo();
    // ... (invoke setter)
    con.update(newMemo);

    final Query query = Query.create();
    // -2-
    final SearchResult<Memo> result = con.findAll(query);
    try {
      for (final Memo memo : result) {
        // ...
      }
    } finally { // Closeable implementation
      result.close();
    }

    final Memo memo1 = new Memo();
    final Memo memo2 = new Memo();
    // -3-
    con.update(memo1, memo2);

    final Memo memo = new Memo();
    // -4-
    con.delete(memo);

    // -5-
    con.delete(query);

  }

  public void query1(final Connection<Memo> connection) throws DataAccessException {
    // -1-
    final Query query = Query.create();
    final SearchResult<Memo> result = connection.findAll(query);
    // -2-
    for (final Memo memo : result) {
      // use the memo
    }
    // -3-
    // Get all objects.
    final List<Memo> memos = result.getAsList();
    // -4-
    // Get only the objects at index 10 (including) to 20 (excluding).
    final List<Memo> memosSub = result.getAsList(10, 20);
  }

  public void query2(final SearchCriterion searchCriterion) throws DataAccessException {
    // -1-
    final Query query = Query.create() // default values
            .only(10) // maximum count of results 
            .only(TextMemo.class, HtmlMemo.class) // sub types
            .only(searchCriterion); // search criteria

  }

  public void searchCriterionBuilder() {
    // @formatter:off
    // -1-
    final Query query = Query.create().only(
      field( "subject" ).asString().contains( "IMPORTANT" )
      .and().
      field( "sender" ).asCaseInsensitiveString().startsWith( "max." )
    );
    // -2-
    // @formatter:n
  }
  
  public void matches(final ApplicationContext ctx, final Memo memo) throws ConfigurationException, DataAccessException, FieldAccessException {
    // @formatter:off
    // -1-
   final boolean isImportant = 
     field( "subject" ).asString().contains( "IMPORTANT" ).matches( ctx, memo );
   // -2-
   // @formatter:n
  }

}
