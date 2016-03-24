package de.ars.daojones.internal.runtime.configuration;

import java.io.Serializable;
import java.util.ServiceLoader;

import de.ars.daojones.runtime.configuration.context.ConfigurationModel;
import de.ars.daojones.runtime.configuration.context.ConfigurationModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A normalizer that invokes normalizers that are registered using the Java
 * Service Loader mechanism.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <Id>
 *          the id type of the model
 * @param <T>
 *          the model type
 * @param <MM>
 *          the model manager type
 */
public class ServiceLoaderCombinatedNormalizer<Id extends Serializable, T extends ConfigurationModel<Id>, MM extends ConfigurationModelManager<Id, T>, MN extends ModelNormalizer<Id, T, MM>>
        implements ModelNormalizer<Id, T, MM> {

  private ServiceLoader<MN> normalizers;

  public ServiceLoaderCombinatedNormalizer( final ClassLoader cl, final Class<MN> modelNormalizerClass ) {
    super();
    normalizers = ServiceLoader.load( modelNormalizerClass, cl );
  }

  @Override
  public void normalize( final T model, final MM beanModelManager ) throws ConfigurationException {
    // Normalize
    for ( final MN normalizer : normalizers ) {
      normalizer.normalize( model, beanModelManager );
    }
  }

}
