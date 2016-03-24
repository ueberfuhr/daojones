package de.ars.daojones.runtime.test.junit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import de.ars.daojones.internal.runtime.test.Constants;
import de.ars.daojones.internal.runtime.test.junit.DaoJonesContextBuilder;
import de.ars.daojones.internal.runtime.test.junit.Injector.InjectionContext;
import de.ars.daojones.internal.runtime.test.utilities.Messages;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * A test runner that runs JUnit tests in the context of a connection
 * configuration. Test classes that are executed with this runner
 * <ul>
 * <li>must provide a public default constructor</li>
 * <li>can have fields annotated with {@link Inject} to get DaoJones artifacts
 * injected</li>
 * <li>can have test method parameters that can be annotated with {@link Inject}
 * (they are always injected)</li>
 * </ul>
 * <b>Please note:</b> It is also possible to use the inner class {@link Rule}
 * as {@link TestRule} to be more flexible.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DaoJones extends BlockJUnit4ClassRunner {

  private static final Messages bundle = Messages.create( "junit.DaoJones" );

  // Contains the DaoJones context
  // Instance variable: singleton PER test method execution.
  private DaoJonesContextBuilder delegate;

  /**
   * Returns the path to the model XML file for the test-default application.
   * This file is searched within the classpath relative to the test class.
   * 
   * @param testClass
   *          the test class
   * @return the path to the model XML file
   */
  public static String getModelFile( final Class<?> testClass ) {
    return DaoJonesContextBuilder.getModelFile( testClass );
  }

  /**
   * Returns the path to the model XML file for a given application. This file
   * is searched within the classpath relative to the test class.
   * 
   * @param testClass
   *          the test class
   * @param application
   *          the application id
   * @return the path to the model XML file
   */
  public static String getModelFile( final Class<?> testClass, final String application ) {
    return DaoJonesContextBuilder.getModelFile( testClass, application );
  }

  /**
   * Creates an instance.
   * 
   * @param testClass
   *          the test class
   * @throws InitializationError
   */
  public DaoJones( final Class<?> testClass ) throws InitializationError {
    super( testClass );
  }

  @Override
  protected void validateConstructor( final List<Throwable> errors ) {
    validateOnlyOneConstructor( errors );
    // Constructor Parameters are allowed
    // validateZeroArgConstructor( errors );
  }

  protected DaoJonesContext getContext() {
    return delegate.getContext();
  }

  protected InjectionContext createInjectionContext( final Inject inject ) {
    return new InjectionContext() {

      @Override
      public DaoJonesContext getContext() {
        return DaoJones.this.getContext();
      }

      @Override
      public String getApplication() {
        return null != inject ? inject.application() : Constants.DEFAULT_APPLICATION;
      }
    };
  }

  @Override
  protected void runChild( final FrameworkMethod method, final RunNotifier notifier ) {
    try {
      delegate = new DaoJonesContextBuilder( getTestClass().getJavaClass(), method.getMethod() );
    } catch ( final Exception e ) {
      notifier.fireTestFailure( new Failure( getDescription(), e ) );
      return;
    }
    // run test
    try {
      super.runChild( method, notifier );
    } finally {
      try {
        delegate.close();
      } catch ( final IOException e ) {
        DaoJones.bundle.log( Level.WARNING, e, "error.context.close" );
      }
      delegate = null;
    }
  }

  /*
   * Invoked by runChild() before the invocation of the test method.
   * The variable "delegate" will be initialized correctly. 
   */
  @Override
  public Object createTest() throws Exception {
    // Constructor Injection
    final Constructor<?> constructor = getTestClass().getOnlyConstructor();
    final Object[] parameters;
    try {
      parameters = getParametersForInvocation( constructor.getParameterTypes(), constructor.getParameterAnnotations() );
    } catch ( final Exception e ) {
      throw new InjectionException( DaoJones.bundle.get( "error.inject.constructor", getTestClass().getJavaClass()
              .getName() ), e );
    }
    final Object result = constructor.newInstance( parameters );
    // Field Injection
    final List<FrameworkField> fields = getTestClass().getAnnotatedFields( Inject.class );
    for ( final FrameworkField ffield : fields ) {
      final Field field = ffield.getField();
      final Inject inject = field.getAnnotation( Inject.class );
      final boolean accessible = field.isAccessible();
      if ( !accessible ) {
        field.setAccessible( true );
      }
      try {
        final InjectionContext ic = createInjectionContext( inject );
        try {
          final Object value = delegate.getInjectedValue( ic, field.getType() ); // TODO "an error occured during injection of...
          field.set( result, value );
        } catch ( final Exception e ) {
          throw new InjectionException( DaoJones.bundle.get( "error.inject.field", field.getDeclaringClass().getName(),
                  field.getName() ), e );
        }
      } finally {
        if ( !accessible ) {
          field.setAccessible( false );
        }
      }
    }
    delegate.setTest( result );
    return result;
  }

  private static class InjectionException extends Exception {

    private static final long serialVersionUID = 1L;

    public InjectionException( final String message, final Throwable cause ) {
      super( message, cause );
    }

  }

  protected Object[] getParametersForInvocation( final Class<?>[] parameterTypes,
          final Annotation[][] parameterAnnotations ) throws Exception {
    final Object[] parameters = new Object[parameterTypes.length];
    for ( int i = 0; i < parameterTypes.length; i++ ) {
      final Annotation[] annotations = parameterAnnotations[i];
      Inject inject = null;
      for ( final Annotation a : annotations ) {
        if ( Inject.class.isAssignableFrom( a.getClass() ) ) {
          inject = ( Inject ) a;
          break;
        }
      }
      final InjectionContext ic = createInjectionContext( inject );
      parameters[i] = delegate.getInjectedValue( ic, parameterTypes[i] );
    }
    return parameters;
  }

  /*
   * Method Invocation with parameters
   */
  @Override
  protected Statement methodInvoker( final FrameworkMethod method, final Object test ) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        // Method Injection
        final Method member = method.getMethod();
        final Object[] parameters;
        try {
          parameters = getParametersForInvocation( member.getParameterTypes(), member.getParameterAnnotations() );
        } catch ( final Exception e ) {
          throw new InjectionException( DaoJones.bundle.get( "error.inject.method", member.getDeclaringClass()
                  .getName(), member.getName() ), e );
        }
        method.invokeExplosively( test, parameters );
      }
    };
  }

  /* *************************
   *        R U L E S        *
   ************************* */

  /**
   * In interface for a {@link TestRule} that configures the DaoJones context.
   * To create an instance, use {@link DaoJones#asRule()}.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @see DaoJones#asRule()
   */
  public interface Rule extends TestRule {

    /**
     * Returns the DaoJones context.
     * 
     * @return the DaoJones context
     */
    DaoJonesContext getContext();

    /**
     * Returns the DaoJones configuration.
     * 
     * @return the DaoJones configuration
     */
    DaoJonesContextConfiguration getConfig();

    /**
     * Returns the connection provider for the default application.
     * 
     * @return the connection provider for the default application
     * @see Constants#DEFAULT_APPLICATION
     */
    ConnectionProvider getConnectionProvider();

    /**
     * Returns the connection provider for the given application.
     * 
     * @param application
     *          the application id
     * @return the connection provider for the given application
     */
    ConnectionProvider getConnectionProvider( String application );

  }

  private static class DescriptionAsAnnotatedElement implements AnnotatedElement {
    private final Description description;

    public DescriptionAsAnnotatedElement( final Description description ) {
      super();
      this.description = description;
    }

    @Override
    public <T extends Annotation> T getAnnotation( final Class<T> annotationClass ) {
      return description.getAnnotation( annotationClass );
    }

    @Override
    public Annotation[] getAnnotations() {
      final Collection<Annotation> annotations = description.getAnnotations();
      return annotations.toArray( new Annotation[annotations.size()] );
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
      return getAnnotations();
    }

    @Override
    public boolean isAnnotationPresent( final Class<? extends Annotation> annotationClass ) {
      return null != getAnnotation( annotationClass );
    }

  }

  private static class RuleImpl implements Rule {

    private final Object test;
    private DaoJonesContextBuilder delegate;

    public RuleImpl( final Object test ) {
      super();
      this.test = test;
    }

    /*
     * Invoked once before the method invocation or (if any) before the instance initialization methods
     */
    @Override
    public Statement apply( final Statement base, final Description description ) {

      return new Statement() {
        @Override
        public void evaluate() throws Throwable {
          // Initialize the context
          final Class<?> testClass = description.getTestClass();
          delegate = new DaoJonesContextBuilder( testClass, !description.isSuite() ? new DescriptionAsAnnotatedElement(
                  description ) : null );
          try {
            delegate.setTest( test );
            // run test
            base.evaluate();
          } finally {
            try {
              delegate.close();
            } catch ( final IOException e ) {
              DaoJones.bundle.log( Level.WARNING, e, "error.context.close" );
            }
            delegate = null;
          }
        }
      };
    }

    @Override
    public DaoJonesContext getContext() {
      return delegate.getContext();
    }

    @Override
    public DaoJonesContextConfiguration getConfig() {
      return getContext().getConfiguration();
    }

    @Override
    public ConnectionProvider getConnectionProvider() {
      return getConnectionProvider( Constants.DEFAULT_APPLICATION );
    }

    @Override
    public ConnectionProvider getConnectionProvider( final String application ) {
      return getContext().getApplication( application );
    }

  }

  /**
   * Returns a {@link TestRule} that automatically initializes the context by
   * reading the annotations of the test class or (if not existing) by using
   * default configuration values.
   * 
   * @param test
   *          the test instance
   * @return the {@link TestRule} object that can be assigned to an instance
   *         variable that is annotated with {@link org.junit.Rule}.
   */
  public static Rule asRule( final Object test ) {
    return new RuleImpl( test );
  }

  /**
   * Returns a {@link TestRule} that automatically initializes the context by
   * reading the annotations of the test class or (if not existing) by using
   * default configuration values. This initializes a class level rule that is
   * not able to access e.g. non-static test models.
   * 
   * @param test
   *          the test instance
   * @return the {@link TestRule} object that can be assigned to an instance
   *         variable that is annotated with {@link org.junit.Rule}.
   */
  public static Rule asClassLevelRule() {
    return new RuleImpl( null );
  }

}
