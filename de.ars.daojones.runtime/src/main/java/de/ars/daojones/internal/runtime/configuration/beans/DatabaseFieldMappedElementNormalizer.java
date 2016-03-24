package de.ars.daojones.internal.runtime.configuration.beans;

import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.Constructor;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.Field;
import de.ars.daojones.runtime.configuration.beans.Method;
import de.ars.daojones.runtime.configuration.beans.MethodParameter;
import de.ars.daojones.runtime.configuration.beans.MethodResult;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public abstract class DatabaseFieldMappedElementNormalizer implements BeanModelNormalizer {

  @Override
  public void normalize( final BeanModel model, final BeanModelManager beanModelManager ) throws ConfigurationException {
    final Bean bean = model.getBean();
    final Constructor constructor = bean.getConstructor();
    if ( null != constructor ) {
      for ( final MethodParameter param : constructor.getParameters() ) {
        normalize( param, beanModelManager );
      }
    }
    for ( final Field field : bean.getFields() ) {
      normalize( field, beanModelManager );
    }
    for ( final Method method : bean.getMethods() ) {
      for ( final MethodParameter param : method.getParameters() ) {
        normalize( param, beanModelManager );
      }
      final MethodResult result = method.getResult();
      if ( null != result ) {
        normalize( result, beanModelManager );
      }
    }
    finish( bean, beanModelManager );
  }

  protected abstract void normalize( DatabaseFieldMappedElement model, final BeanModelManager beanModelManager )
          throws ConfigurationException;

  protected void finish( final Bean model, final BeanModelManager beanModelManager ) throws ConfigurationException {

  }

}
