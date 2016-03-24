package de.ars.daojones.runtime;

import static de.ars.daojones.LoggerConstants.DEBUG;
import static de.ars.daojones.LoggerConstants.ERROR;
import static de.ars.daojones.LoggerConstants.getLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import de.ars.daojones.Parameter;
import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.ApplicationContextFactory;

/**
 * A factory that instantiates beans by using their implementations.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@SuppressWarnings( "unchecked" )
public class BeanFactory {

  /* *************************************************
   *          U T I L I T Y   M E T H O D S          *
   ************************************************* */

  private static <T> Constructor<T> findConstructor( Class<?> c,
      Parameter<?>... parameters ) throws NoSuchMethodException {
    final Class[] classes = new Class[parameters.length];
    int index = 0;
    for ( Parameter<?> parameter : parameters ) {
      classes[index++] = parameter.getType();
    }
    return ( Constructor<T> ) c.getDeclaredConstructor( classes );
  }

  private static void handleException( Exception e )
      throws BeanCreationException {
    getLogger().log( ERROR, "An error occured.", e );
    throw new BeanCreationException( e );
  }

  private static Parameter[] objectsToParameters( Object[] objects ) {
    Parameter[] parameters = new Parameter[objects.length];
    int index = 0;
    for ( Object o : objects ) {
      parameters[index++] = new Parameter<Object>( o );
    }
    return parameters;
  }

  /* *************************************************
   *          F A C T O R Y   M E T H O D S          *
   ************************************************* */

  /**
   * Returns the class containing the implemented methods of the given abstract
   * bean class.
   * 
   * @param <T>
   *          the type
   * @param beanClass
   *          the abstract class
   * @return the class containing implemented methods
   * @throws BeanLoadingException
   */
  public static <T extends Dao> Class<? extends T> getImplementationClass(
      Class<T> beanClass ) throws BeanLoadingException {
    return BeanImplementationFactory.getInstance().getImplementationClass(
        beanClass );
  }

  /**
   * Creates a bean by the usage of another template bean. The
   * {@link DataObject} and the {@link ApplicationContext} is resolved from this
   * template bean.
   * 
   * @param <T>
   *          the bean type
   * @param c
   *          the bean class
   * @param template
   *          the template bean
   * @param parameters
   *          the constructor parameters
   * @throws BeanCreationException
   * @return the bean instance
   */
  public static <T extends Dao> T createBean( Class<T> c, Dao template,
      Parameter<?>... parameters ) throws BeanCreationException {
    final ApplicationContext ctx = ApplicationContextFactory.getInstance()
        .getApplicationContext( template.getApplicationContextId() );
    return BeanFactory
        .createBean( c, template.getDataObject(), ctx, parameters );
  }

  /**
   * Creates a bean.
   * 
   * @param <T>
   *          the bean type
   * @param c
   *          the bean class
   * @param dataObject
   *          the {@link DataObject} containing source information
   * @param ctx
   *          the {@link ApplicationContext}
   * @param parameters
   *          the constructor parameters
   * @throws BeanCreationException
   * @return the bean instance
   */
  public static <T extends Dao> T createBean( Class<T> c,
      DataObject dataObject, ApplicationContext ctx, Object... parameters )
      throws BeanCreationException {
    final boolean hasParameters = !( null == parameters || 0 == parameters.length );
    return createBean( c, dataObject, ctx,
        ( Parameter[] ) ( hasParameters ? objectsToParameters( parameters )
            : null ) );
  }

  /**
   * Creates a bean.
   * 
   * @param <T>
   *          the bean type
   * @param c
   *          the bean class
   * @param dataObject
   *          the {@link DataObject} containing source information
   * @param ctx
   *          the {@link ApplicationContext}
   * @param parameters
   *          the constructor parameters
   * @throws BeanCreationException
   * @return the bean instance
   */
  public static <T extends Dao> T createBean( Class<T> c,
      DataObject dataObject, ApplicationContext ctx, Parameter<?>... parameters )
      throws BeanCreationException {
    if ( getLogger().isLoggable( DEBUG ) )
      getLogger().log(
          DEBUG,
          "Create DaoJones Bean: " + c.getName() + " (Parameters: "
              + parameters + ")" );
    T result = null;
    try {
      Class concreteClass = null;
      try {
        concreteClass = getImplementationClass( c );
      } catch ( BeanLoadingException e ) {
        // Use class directly -> See Task 525
        concreteClass = c;
        if ( ( c.getModifiers() | Modifier.ABSTRACT ) > 0 )
          throw e;
      }
      if ( !c.isAssignableFrom( concreteClass ) )
        throw new BeanCreationException( "The class " + concreteClass.getName()
            + " is not a valid subclass of class " + c.getName() + "!" );
      final Class<T> castedConcreteClass = ( Class<T> ) concreteClass;
      if ( null == parameters )
        parameters = new Parameter[0];
      final Constructor<T> constructor = findConstructor( castedConcreteClass,
          parameters );
      final boolean accessible = constructor.isAccessible();
      if ( !accessible )
        constructor.setAccessible( true );
      try {
        final Object[] values = new Object[parameters.length];
        for ( int i = 0; i < values.length; i++ )
          values[i] = parameters[i].getValue();
        result = constructor.newInstance( values );
      } finally {
        if ( !accessible )
          constructor.setAccessible( false );
      }
      result.setDataObject( dataObject );
      result.setApplicationContextId( ctx.getId() );
      result.refresh();
    } catch ( BeanLoadingException e ) {
      handleException( e );
    } catch ( IllegalAccessException e ) {
      handleException( e );
    } catch ( InstantiationException e ) {
      handleException( e );
    } catch ( IllegalArgumentException e ) {
      handleException( e );
    } catch ( InvocationTargetException e ) {
      handleException( e );
    } catch ( NoSuchMethodException e ) {
      handleException( e );
    } catch ( DataAccessException e ) {
      handleException( e );
    }
    return result;
  }

}
