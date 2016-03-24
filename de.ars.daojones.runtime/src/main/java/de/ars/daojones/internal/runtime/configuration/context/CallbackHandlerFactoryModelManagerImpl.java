package de.ars.daojones.internal.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModel;
import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModelManager;

public class CallbackHandlerFactoryModelManagerImpl extends
        ConfigurationModelManagerImpl<String, CallbackHandlerFactoryModel> implements
        CallbackHandlerFactoryModelManager {

  public CallbackHandlerFactoryModelManagerImpl( final Class<CallbackHandlerFactoryModel> modelClass ) {
    super( modelClass );
  }

}
