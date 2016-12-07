package de.ars.daojones.runtime.test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.spi.database.Connection;
import de.ars.daojones.runtime.spi.database.ConnectionBuildException;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.ConnectionFactory;
import de.ars.daojones.runtime.spi.database.CredentialVaultException;
import de.ars.daojones.runtime.test.data.Model;
import de.ars.daojones.runtime.test.spi.database.TestConnection;

/**
 * A connection factory that creates connections to a dummy database. Test
 * driver providers can create a subclass and overwrite
 * {@link #createTestConnection(ConnectionContext, Model)} to create an instance
 * of a subclass of the default test connection.
 *
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class TestConnectionFactory implements ConnectionFactory {

  private final Map<BeanModel.Id, Connection<?>> connectionPerBean = new HashMap<BeanModel.Id, Connection<?>>();
  private final Map<ConnectionModel.Id, Model> contents = new HashMap<ConnectionModel.Id, Model>();

  private TestModelResolver resolver = new XmlResourceTestModelResolver() {

    @Override
    protected URL resolveResource( final String file ) throws IOException {
      return getClass().getClassLoader().getResource( file );
    }
  };

  private TestConnectionCallbackRequestor callbackRequestor;

  /**
   * Returns the test model resolver.
   *
   * @return the test model resolver
   */
  public TestModelResolver getResolver() {
    return resolver;
  }

  /**
   * Sets the test model resolver.
   *
   * @param resolver
   *          the test model resolver
   */
  public void setResolver( final TestModelResolver resolver ) {
    this.resolver = resolver;
  }

  /**
   * Returns the callback requestor.
   *
   * @return the callback requestor
   */
  protected TestConnectionCallbackRequestor getCallbackRequestor() {
    return callbackRequestor;
  }

  /**
   * Sets the callback requestor.
   *
   * @param callbackRequestor
   *          the callback requestor
   */
  protected void setCallbackRequestor( final TestConnectionCallbackRequestor callbackRequestor ) {
    this.callbackRequestor = callbackRequestor;
  }

  /**
   * Creates an instance of a test connection.
   *
   * @param <T>
   *          the bean type that this connection should handle
   * @param context
   *          the context
   * @param model
   *          the model
   * @return the test connection
   */
  protected <T> TestConnection<T> createTestConnection( final ConnectionContext<T> context, final Model model ) {
    return new TestConnection<T>( context, model );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public final <T> Connection<T> createConnection( final ConnectionContext<T> context )
          throws ConnectionBuildException {
    try {
      final BeanModel.Id id = context.getBeanModel().getId();
      synchronized ( connectionPerBean ) {

        if ( connectionPerBean.containsKey( id ) ) {
          return ( Connection<T> ) connectionPerBean.get( id );
        } else {
          final ConnectionModel connectionModel = context.getConnectionModel();
          final TestConnectionCallbackRequestor callbackRequestor = getCallbackRequestor();
          if ( null != callbackRequestor ) {
            callbackRequestor.callback( connectionModel.getConnection(), context.getCredentialVault() );
          }
          final Model model = readContent( connectionModel );
          final Connection<T> con = createTestConnection( context, model );
          connectionPerBean.put( id, con );
          return con;
        }
      }
    } catch ( final IOException e ) {
      throw new ConnectionBuildException( e );
    } catch ( final CredentialVaultException e ) {
      throw new ConnectionBuildException( e );
    } catch ( final ConfigurationException e ) {
      throw new ConnectionBuildException( e );
    }
  }

  private Model readContent( final ConnectionModel model ) throws IOException {
    final ConnectionModel.Id id = model.getId();
    synchronized ( contents ) {
      if ( contents.containsKey( id ) ) {
        return contents.get( id );
      } else {
        final Model content = getResolver().resolveModel( model );
        contents.put( id, content );
        return content;
      }
    }
  }

}
