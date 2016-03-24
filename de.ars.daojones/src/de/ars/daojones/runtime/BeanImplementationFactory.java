package de.ars.daojones.runtime;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


/**
 * A helper class providing access to bean implementations.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class BeanImplementationFactory {

  private static final String CL_SETTING = "bean.classloader";

  private final Map<ClassLoader, ClassLoader> implementationClassLoaders = new HashMap<ClassLoader, ClassLoader>();
  /*
   * Can be singleton here:
   *  - in multi-user environment one instance for the whole application
   *  - in a cluster one instance per server instance
   * Cannot be used with the usage of one classloader for multiple application instances.
   */
  private static BeanImplementationFactory theInstance;

  /**
   * Creates an instance.
   */
  private BeanImplementationFactory() {
    super();
  }

  /**
   * Returns the instance.
   * 
   * @return the instance
   */
  public static synchronized BeanImplementationFactory getInstance() {
    if ( null == theInstance ) {
      theInstance = new BeanImplementationFactory();
    }
    return theInstance;
  }

  /**
   * Returns the class loader for loading the implementation class.
   * 
   * @param beanClass
   *          the bean class
   * @return the class loader for loading the implementation class
   * @throws BeanLoadingException
   */
  @SuppressWarnings( "unchecked" )
  public ClassLoader getImplementationClassloader(
      Class<? extends Dao> beanClass ) throws BeanLoadingException {
    final ClassLoader parent = beanClass.getClassLoader();
    synchronized ( this.implementationClassLoaders ) {
      if ( !this.implementationClassLoaders.containsKey( parent ) ) {
        final String classLoaderClassName = Settings.getInstance().get(
            CL_SETTING );
        if ( null != classLoaderClassName ) {
          try {
            final Class<? extends ClassLoader> classLoaderClass = ( Class<? extends ClassLoader> ) BeanImplementationFactory.class
                .getClassLoader().loadClass( classLoaderClassName );
            ClassLoader value = null;
            try {
              final Constructor<? extends ClassLoader> con = classLoaderClass
                  .getConstructor( ClassLoader.class );
              value = con.newInstance( parent );
            } catch ( NoSuchMethodException e ) {
              value = classLoaderClass.newInstance();

            }
            this.implementationClassLoaders.put( parent, value );
          } catch ( Throwable t ) {
            throw new BeanLoadingException( "Unable to load class loader \""
                + classLoaderClassName + "\"!", t );
          }
        } else {
          throw new BeanLoadingException( "Runtime property \"" + CL_SETTING
              + "\" is not set!" );
        }
      }
    }
    return this.implementationClassLoaders.get( parent );
  }

  /**
   * Loads the bean implementation class of a bean class.
   * 
   * @param <T>
   *          the bean type
   * @param beanClass
   *          the bean class
   * @return the bean implementation class
   * @throws BeanLoadingException
   */
  @SuppressWarnings( "unchecked" )
  public <T extends Dao> Class<? extends T> getImplementationClass(
      Class<T> beanClass ) throws BeanLoadingException {
    final ClassLoader cl = getImplementationClassloader( beanClass );
    if ( null != cl ) {
      try {
        final String beanClassName = beanClass.getName();
        final String implementationClassName = Naming
            .getImplementationClassName( beanClassName );
        return ( Class<? extends T> ) cl.loadClass( implementationClassName );
      } catch ( ClassNotFoundException e ) {
        throw new BeanLoadingException( e );
      }
    } else {
      return null;
    }
  }

}
