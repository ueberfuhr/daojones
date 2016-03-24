package de.ars.daojones.internal.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.beans.GlobalConverter;
import de.ars.daojones.runtime.configuration.context.ConverterModelManager;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class ConverterModelManagerImpl extends
        ConfigurationModelManagerImpl<GlobalConverterModel.Id, GlobalConverterModel> implements ConverterModelManager {

  ConverterModelManagerImpl( final Class<GlobalConverterModel> modelClass ) {
    super( modelClass );
  }

  @Override
  public GlobalConverter findConverterFor( final String application, final Class<?> type )
          throws ConfigurationException {
    final GlobalConverterModel.Id id = new GlobalConverterModel.Id( application, type.getName() );
    final GlobalConverterModel model = getModel( id );
    return null != model ? model.getConverter() : null;
  }

}
