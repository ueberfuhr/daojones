package de.ars.daojones.runtime.docs;

//-1-
import static de.ars.daojones.runtime.query.SearchCriterionBuilder.*;
//-2-
import de.ars.daojones.runtime.configuration.provider.AnnotationBeanConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.XmlConnectionConfigurationSource;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;
import de.ars.daojones.runtime.query.Query;

@SuppressWarnings("unused")
public class DaoJonesDemo {

  public static final String APP = "MyFirstApplication";

  public void demo1() throws DataAccessException, ConfigurationException {
    final DaoJonesContextFactory dcf = new DaoJonesContextFactory();
    dcf.setConfigurationSources(
            new AnnotationBeanConfigurationSource(DaoJonesDemo.APP),
            new XmlConnectionConfigurationSource(DaoJonesDemo.APP, DaoJonesDemo.class
                    .getResource("daojones-connections.xml")));
    // <1> Compatible with try-with-resources (since Java 7)
    final DaoJonesContext ctx = dcf.createContext();
    try {
      final ConnectionProvider cp = ctx.getApplication(DaoJonesDemo.APP);
      final Connection<Memo> con = cp.getConnection(Memo.class);
      // <1> Compatible with try-with-resources (since Java 7)
      try {
        // SearchCriteria etwas anders?
        final SearchResult<Memo> memos = con.findAll(Query.create().only(2));
        for (final Memo memo : memos) {
          // Iterate through the search results
          // (Streamed results)

        }
      } finally {
        con.close();
      }
    } finally {
      ctx.close();
    }

    Query.create().only(field("receiver").isEmpty().or().field("sender").isEmpty());
    Query.create().only(not(TRUE()).and().TRUE().or().FALSE());
    Query.create()
            .only(field("title").isEmpty().and().field("text").asString()
                    .startsWith("Hallo")).only(10);
    Query.create().only(
            field("title").isEmpty().or().asString().contains("Tagebuch").and()
                    .startsWith("Mein").or().endsWith("Dein"));
    Query.create().only(
            field("authors").asCollectionOf(String.class)
                    .containsAllOf("Ralf Zahn", "Michael Mueller").or()
                    .containsOneOf("Stefan Schaeffer", "Domink Ebert"));

  }
  //  try ( final DaoJonesContext ctx = dcf.createContext() ) { // <1> Compatible with try-with-resources (since Java 7)
  //    final ConnectionProvider cp = ctx.getApplication( DaoJonesDemo.APP );
  //    try ( final Connection<Memo> con = cp.getConnection( Memo.class ) ) { // <1> Compatible with try-with-resources (since Java 7)
  //      // SearchCriteria etwas anders?
  //      final SearchResult<Memo> memos = con.findAll( Query.create().only( 2 ) );
  //      System.out.println( memos );
  //
  //    }
  //  }

}
