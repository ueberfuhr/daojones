package de.ars.daojones.runtime.beans.fields;

import java.util.List;

import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.Property;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorContext;

/**
 * A transfer object providing context information for a converter invocation.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class ConverterContext {

  // TODO BeanIdentificator?, ObjectLoader? Dependency Injection?
  private final BeanAccessorContext<?> beanAccessorContext;
  private final Object bean;
  private final DatabaseFieldMappedElement descriptor;
  private final Class<?> targetType;
  private final Property[] metadata;

  /**
   * Creates an instance. Clients must not create instances of this class.
   * 
   * @param beanAccessorContext
   *          the bean accessor context
   * @param bean
   *          the bean
   * @param descriptor
   *          the descriptor
   * @param targetType
   *          the type of the target within the bean
   */
  protected ConverterContext( final BeanAccessorContext<?> beanAccessorContext, final Object bean,
          final DatabaseFieldMappedElement descriptor, final Class<?> targetType ) {
    super();
    this.beanAccessorContext = beanAccessorContext;
    this.bean = bean;
    this.descriptor = descriptor;
    this.targetType = targetType;
    final List<Property> metadata = descriptor.getFieldMapping().getMetadata();
    this.metadata = metadata.toArray( new Property[metadata.size()] );
  }

  /**
   * Returns the bean accessor context
   * 
   * @return the bean accessor context
   */
  protected BeanAccessorContext<?> getBeanAccessorContext() {
    return beanAccessorContext;
  }

  /**
   * Creates a derived bean accessor context for the given bean type.
   * 
   * @param beanType
   *          the bean type
   * @return the derived bean accessor context
   * @throws ConfigurationException
   */
  protected <E> BeanAccessorContext<E> createBeanAccessorContextFor( final Class<E> beanType )
          throws ConfigurationException {
    return BeanAccessorContext.createSubContextFor( getBeanAccessorContext(), beanType );
  }

  /**
   * Returns the effective bean model.
   * 
   * @return the effective bean model
   */
  public BeanModel getModel() {
    return getBeanAccessorContext().getModel();
  }

  /**
   * Returns the meta data of the field mapping.
   * 
   * @return the meta data of the field mapping
   */
  public Property[] getMetadata() {
    return metadata;
  }

  /**
   * Returns the connection provider.
   * 
   * @return the connection provider
   */
  public ConnectionProvider getConnectionProvider() {
    return getBeanAccessorContext().getConnectionProvider();
  }

  /**
   * Returns the bean.<br/>
   * <br/>
   * <b>Please note:</b> A converter that converts a constructor parameter will
   * get a bean value of <tt>null</tt>.
   * 
   * @return the bean
   */
  public Object getBean() {
    return bean;
  }

  /**
   * Returns the descriptor.
   * 
   * @return the descriptor
   */
  public DatabaseFieldMappedElement getDescriptor() {
    return descriptor;
  }

  /**
   * Returns the type of the target within the bean.
   * 
   * @return the type of the target within the bean
   */
  public Class<?> getTargetType() {
    return targetType;
  }

}
