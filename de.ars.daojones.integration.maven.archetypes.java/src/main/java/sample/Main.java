package sample;

import de.ars.daojones.runtime.configuration.provider.AnnotationBeanConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.XmlConnectionConfigurationSource;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;

public class Main {

  private static final String APP = "main-app";
  private static final String CONNECTIONS_CONFIGURATION = "/META-INF/daojones-connections.xml";

  public static void main( final String[] args ) throws ConfigurationException, DataAccessException {
    final DaoJonesContextFactory dcf = new DaoJonesContextFactory();
    dcf.setConfigurationSources( // to configure:
            // scan annotations for bean model
            new AnnotationBeanConfigurationSource( Main.APP ),
            // scan annotations for bean model
            new XmlConnectionConfigurationSource( Main.APP,
                    Main.class.getResource( Main.CONNECTIONS_CONFIGURATION ) ) );
    try ( final DaoJonesContext ctx = dcf.createContext() ) {
      final ConnectionProvider cp = ctx.getApplication( Main.APP );
      final MemoController controller = new MemoController( cp );
      // find all memos whose subject starts with "IMPORTANT"
      try ( final SearchResult<Memo> importantMemos = controller.getImportantMemos() ) {
        for ( final Memo memo : importantMemos ) {
          System.out.println( memo );
        }
      }
    }
  }

}
