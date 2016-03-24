package de.ars.daojones.runtime.test;

import java.io.IOException;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.test.data.Model;

/**
 * Instances of this type are responsible to resolve the test model from one ore
 * multiple sources.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface TestModelResolver {

  /**
   * Resolves the model. This method is invoked by the test connection during
   * initialization.
   * 
   * @param model
   *          the connection model
   * @return the model
   * @throws IOException
   *           if resolving the model occured an error
   */
  Model resolveModel( final ConnectionModel model ) throws IOException;

}
