package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.ars.daojones.internal.runtime.configuration.beans.InstanceProviderAdapter;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A model element that provides a fully-configured instance instead of a simple
 * class name that has to be initialized by DaoJones (like the
 * {@link TypeMappedElement} provides).
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 * @param <T>
 *          the type of the instance that is provided
 * @see TypeMappedElement
 */
@XmlAccessorType( XmlAccessType.FIELD )
public abstract class InstanceProvidingElement<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * The provider of the instance. This could be a simple instance holder or an
   * instance factory that returns a new instance for each invocation.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
   * @since 2.0
   * @param <T>
   *          the type of the instance that is provided
   */
  public interface InstanceProvider<T> extends Serializable {
    /**
     * Returns the fully-configured instance.
     * 
     * @param classLoader
     *          the class loader
     * @return the fully-configured instance
     * @throws ConfigurationException
     *           if the instance could not get configured
     */
    T getInstance( ClassLoader classLoader ) throws ConfigurationException;

    /**
     * Returns the class name of the instance.
     * 
     * @return the class name of the instance
     * @throws ConfigurationException
     */
    String getInstanceClassName() throws ConfigurationException;

  }

  /**
   * An instance provider that simply loads a named class and creates an
   * instance by invoking the default constructor.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
   * @since 2.0
   */
  public static class InstanceByClassNameProvider implements InstanceProvider<Object> {

    private static final long serialVersionUID = 1L;

    private final String className;

    /**
     * Constructor.
     * 
     * @param className
     *          the class name
     */
    public InstanceByClassNameProvider( final String className ) {
      super();
      this.className = className;
    }

    @Override
    public Object getInstance( final ClassLoader classLoader ) throws ConfigurationException {
      try {
        final Class<?> c = Class.forName( getInstanceClassName(), true, classLoader );
        final InstanceProvider<Object> delegate = new InstanceByClassProvider<Object>( c );
        final Object result = delegate.getInstance( classLoader );
        return result;
      } catch ( final ConfigurationException e ) {
        throw e;
      } catch ( final Exception e ) {
        throw new ConfigurationException( e );
      }
    }

    @Override
    public String getInstanceClassName() throws ConfigurationException {
      return className;
    }

  }

  /**
   * An instance provider that simply creates an instance by invoking the
   * default constructor of the given class.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
   * @since 2.0
   * @param <T>
   *          the instance type
   */
  public static class InstanceByClassProvider<T> implements InstanceProvider<T> {

    private static final long serialVersionUID = 1L;

    private final Class<? extends T> instanceClass;

    /**
     * Constructor.
     * 
     * @param instanceClass
     *          the given instance class
     * @throws IllegalArgumentException
     *           if the instance class is <tt>null</tt>
     */
    public InstanceByClassProvider( final Class<? extends T> instanceClass ) throws IllegalArgumentException {
      super();
      if ( null != instanceClass ) {
        this.instanceClass = instanceClass;
      } else {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public T getInstance( final ClassLoader classLoader ) throws ConfigurationException {
      try {
        final Constructor<? extends T> constructor = this.instanceClass.getConstructor();
        final boolean accessible = constructor.isAccessible();
        synchronized ( constructor ) {
          if ( !accessible ) {
            constructor.setAccessible( true );
          }
          try {
            final T result = constructor.newInstance();
            return result;
          } finally {
            if ( !accessible ) {
              constructor.setAccessible( false );
            }
          }
        }
      } catch ( final Exception e ) {
        throw new ConfigurationException( e );
      }
    }

    @Override
    public String getInstanceClassName() throws ConfigurationException {
      return this.instanceClass.getName();
    }

  }

  /**
   * An instance provider that contains the instance.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
   * @since 2.0
   * @param <T>
   *          the instance type that must be serializable for this type of
   *          instance provider
   */
  public static class DefaultInstanceProvider<T extends Serializable> implements InstanceProvider<T> {

    private static final long serialVersionUID = 1L;

    private final T instance;
    private final String className;

    /**
     * Constructor. The class name, that the instance is managed as, is derived
     * from the instance's type.
     * 
     * @param instance
     *          the instance
     * @throws IllegalArgumentException
     *           if the instance is <tt>null</tt>
     */
    public DefaultInstanceProvider( final T instance ) throws IllegalArgumentException {
      this( instance, null != instance ? instance.getClass().getName() : null );
    }

    /**
     * Constructor.
     * 
     * @param instance
     *          the instance
     * @param className
     *          the name of the class that the instance should be managed as
     * @throws IllegalArgumentException
     *           if the instance or the class name is <tt>null</tt>
     */
    public DefaultInstanceProvider( final T instance, final String className ) throws IllegalArgumentException {
      super();
      if ( null != instance && null != className ) {
        this.instance = instance;
        this.className = className;
      } else {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public T getInstance( final ClassLoader classLoader ) throws ConfigurationException {
      return instance;
    }

    @Override
    public String getInstanceClassName() throws ConfigurationException {
      return className;
    }

  }

  /**
   * An instance provider that uses a delegate to get the instance for the first
   * invocation and then caches this instance internally. After serialization,
   * the instance of removed and the delegate instance provider is invoked
   * again.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
   * @since 2.0
   * @param <T>
   *          the instance type
   */
  public static class SingleInstanceProvider<T> implements InstanceProvider<T> {

    private static final long serialVersionUID = 1L;

    private final InstanceProvider<T> delegate;
    private transient T instance;
    private transient String className;

    /**
     * Constructor.
     * 
     * @param delegate
     *          the delegate instance provider
     */
    public SingleInstanceProvider( final InstanceProvider<T> delegate ) {
      super();
      this.delegate = delegate;
    }

    @Override
    public T getInstance( final ClassLoader classLoader ) throws ConfigurationException {
      synchronized ( this ) {
        if ( null == this.instance ) {
          this.instance = delegate.getInstance( classLoader );
        }
      }
      return this.instance;
    }

    @Override
    public String getInstanceClassName() throws ConfigurationException {
      synchronized ( this ) {
        if ( null == this.className ) {
          this.className = delegate.getInstanceClassName();
        }
      }
      return this.className;
    }

  }

  @XmlJavaTypeAdapter( InstanceProviderAdapter.class )
  @XmlAttribute( name = "type", required = true )
  // type attribute is specified within TypeMappedElement
  private InstanceProvider<T> instanceProvider;

  /**
   * Returns the instance provider.
   * 
   * @return the instance provider
   */
  public InstanceProvider<T> getInstanceProvider() {
    return instanceProvider;
  }

  /**
   * Sets the instance provider.
   * 
   * @param instanceProvider
   *          the instance provider
   */
  public void setInstanceProvider( final InstanceProvider<T> instanceProvider ) {
    this.instanceProvider = instanceProvider;
  }

  /**
   * Returns the fully-configured instance. This method delegates to
   * <tt>getInstanceProvider().getInstance()</tt>.
   * 
   * @param classLoader
   *          the class loader
   * @return the fully-configured instance or <tt>null</tt>, if the instance
   *         provider is not initialized
   * @throws ConfigurationException
   *           if the instance could not get configured
   */
  public T getInstance( final ClassLoader classLoader ) throws ConfigurationException {
    return null != getInstanceProvider() ? getInstanceProvider().getInstance( classLoader ) : null;
  }

}
