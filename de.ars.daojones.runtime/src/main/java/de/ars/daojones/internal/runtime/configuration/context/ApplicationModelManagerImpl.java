package de.ars.daojones.internal.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.ApplicationModelManager;

public class ApplicationModelManagerImpl extends ConfigurationModelManagerImpl<String, ApplicationModel> implements
        ApplicationModelManager {

  public ApplicationModelManagerImpl( final Class<ApplicationModel> modelClass ) {
    super( modelClass );
  }

}
