package de.ars.daojones.runtime;

/**
 * A helper class providing naming conventions.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class Naming {

  private static final String IMPL_SUFFIX = "_DaoJones_Impl";

  private Naming() {
    super();
  }

  /**
   * Returns the fully qualified name of the bean implementation class.
   * 
   * @param beanClassName
   *          the annotated bean interface or class
   * @return the fully qualified name of the bean implementation class
   */
  public static String getImplementationClassName( String beanClassName ) {
    final StringBuffer sb = new StringBuffer( beanClassName );
    sb.append( IMPL_SUFFIX );
    return sb.toString();
  }

  /**
   * Returns a flag indicating whether the given class name is a bean
   * implementation class name.
   * 
   * @param className
   *          the class name
   * @return true if the given class name is a bean implementation class name
   */
  public static boolean isImplementationClassName( String className ) {
    return null != className && className.endsWith( IMPL_SUFFIX )
        && !className.equals( IMPL_SUFFIX )
        && !className.endsWith( "." + IMPL_SUFFIX );
  }

  /**
   * Returns the bean class name.
   * 
   * @param implementationClassName
   *          the implementation class name.
   * @return the bean class name or null if the given class name is not an
   *         implementation class name.
   */
  public static String getBeanClassName( String implementationClassName ) {
    return isImplementationClassName( implementationClassName ) ? implementationClassName
        .substring( 0, implementationClassName.length() - IMPL_SUFFIX.length() )
        : null;
  }

}
