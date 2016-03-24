package de.ars.daojones.internal.runtime.configuration.connections;

import de.ars.daojones.internal.runtime.security.StaticCallbackHandlerFactoryModel;
import de.ars.daojones.runtime.configuration.connections.Connection;
import de.ars.daojones.runtime.configuration.connections.Credential;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * Sets a default credential and a credential id, if the credential does not
 * provide any.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class CredentialNormalizer implements ConnectionModelNormalizer {

  private static int counter = 0;

  @Override
  public void normalize( final ConnectionModel model, final ConnectionModelManager modelManager )
          throws ConfigurationException {
    final Connection connection = model.getConnection();
    Credential credential = connection.getCredential();
    if ( null == credential ) {
      credential = new Credential();
      credential.setType( StaticCallbackHandlerFactoryModel.ID );
      connection.setCredential( credential );
    }
    if ( null == credential.getId() ) {
      credential.setId( "cred_" + model.getId().getApplicationId() + "_" + System.currentTimeMillis()
              + ( CredentialNormalizer.counter++ ) );
    }
  }
}
