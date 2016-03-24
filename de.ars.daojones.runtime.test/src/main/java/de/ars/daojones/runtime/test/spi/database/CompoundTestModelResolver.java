package de.ars.daojones.runtime.test.spi.database;

import java.io.IOException;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.test.TestModelResolver;
import de.ars.daojones.runtime.test.data.Model;

/**
 * A test model resolver that delegates to multiple test model resolvers to
 * create a compound test model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class CompoundTestModelResolver implements TestModelResolver {

  private final TestModelResolver[] resolvers;

  /**
   * Constructor.
   * 
   * @param resolvers
   *          the resolvers
   */
  public CompoundTestModelResolver( final TestModelResolver... resolvers ) {
    super();
    this.resolvers = resolvers;
  }

  @Override
  public Model resolveModel( final ConnectionModel model ) throws IOException {
    final Model result = new Model();
    for ( final TestModelResolver resolver : resolvers ) {
      final Model rModel = resolver.resolveModel( model );
      result.getDataSources().addAll( rModel.getDataSources() );
    }
    return result;
  }

}
