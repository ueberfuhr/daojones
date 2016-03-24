package de.ars.daojones.internal.runtime.configuration.beans;

import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.Constructor;
import de.ars.daojones.runtime.configuration.beans.Invocable;
import de.ars.daojones.runtime.configuration.beans.InvocableElement;
import de.ars.daojones.runtime.configuration.beans.Method;
import de.ars.daojones.runtime.configuration.beans.MethodParameter;
import de.ars.daojones.runtime.configuration.beans.MethodResult;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class InvocableElementNormalizer implements BeanModelNormalizer {

  @Override
  public void normalize( final BeanModel model, final BeanModelManager beanModelManager ) throws ConfigurationException {
    final Bean bean = model.getBean();
    final Constructor constructor = bean.getConstructor();
    if ( null != constructor ) {
      for ( final MethodParameter param : constructor.getParameters() ) {
        normalize( param, constructor );
      }
    }
    for ( final Method method : bean.getMethods() ) {
      for ( final MethodParameter param : method.getParameters() ) {
        normalize( param, method );
      }
      final MethodResult result = method.getResult();
      if ( null != result ) {
        normalize( result, method );
      }
    }
  }

  protected void normalize( final InvocableElement element, final Invocable invokable ) {
    element.setInvocable( invokable );
  }

}
