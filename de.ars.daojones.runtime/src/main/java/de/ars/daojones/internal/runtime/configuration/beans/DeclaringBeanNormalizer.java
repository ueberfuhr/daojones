package de.ars.daojones.internal.runtime.configuration.beans;

import java.util.Collection;

import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.BeanMember;
import de.ars.daojones.runtime.configuration.beans.Constructor;
import de.ars.daojones.runtime.configuration.beans.Method;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A normalizer that sets the declaring bean property to {@link BeanMember}
 * instances.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DeclaringBeanNormalizer implements BeanModelNormalizer {

  @Override
  public void normalize( final BeanModel model, final BeanModelManager beanModelManager ) throws ConfigurationException {
    final Bean bean = model.getBean();
    normalize( bean, bean.getIdField() );
    normalize( bean, bean.getFields() );
    final Constructor c = bean.getConstructor();
    if ( null != c ) {
      normalize( bean, c );
      normalize( bean, c.getParameters() );
    }
    for ( final Method method : bean.getMethods() ) {
      normalize( bean, method );
      normalize( bean, method.getParameters() );
      normalize( bean, method.getResult() );
    }
  }

  private void normalize( final Bean bean, final Collection<? extends BeanMember> beanMembers ) {
    normalize( bean, beanMembers.toArray( new BeanMember[beanMembers.size()] ) );
  }

  /**
   * Sets the declaring bean to a couple of bean members.
   * 
   * @param bean
   *          the declaring bean
   * @param beanMembers
   *          the bean members
   */
  protected void normalize( final Bean bean, final BeanMember... beanMembers ) {
    if ( null != beanMembers ) {
      for ( final BeanMember beanMember : beanMembers ) {
        // Only for bean members that do not contain any declaring bean
        if ( null != beanMember && null == beanMember.getDeclaringBean() ) {
          beanMember.setDeclaringBean( bean );
        }
      }
    }
  }

}
