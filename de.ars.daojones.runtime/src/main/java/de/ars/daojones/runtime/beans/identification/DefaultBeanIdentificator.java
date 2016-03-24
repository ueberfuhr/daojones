package de.ars.daojones.runtime.beans.identification;

import java.lang.reflect.Field;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.internal.runtime.utilities.TypeHierarchyVisitor;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.IdField;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * Default bean identificator that uses the field, that is marked as id, to read
 * and store the id of the bean within the database.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DefaultBeanIdentificator implements BeanIdentificator {

  private static final Messages logger = Messages.create( "beans.identificator.Default" );

  // We use this search for the type to handle classes loaded by different classloaders. 
  private static Class<?> findAncestorClass( final Class<?> c, final String name ) {
    final ThreadLocal<Class<?>> result = new ThreadLocal<Class<?>>();
    new TypeHierarchyVisitor() {

      @Override
      protected boolean visit( final Class<?> c ) {
        if ( name.equals( c.getName() ) ) {
          result.set( c );
          return false;
        } else {
          return true;
        }
      }
    }.accept( c );
    return result.get();
  }

  /**
   * Finds the field that contains the id.
   * 
   * @param model
   *          the bean model
   * @param bean
   *          the bean
   * @return the field or <tt>null</tt>, if there isn't any id field
   * @throws DataAccessException
   *           if reading the id field fails
   * @throws ConfigurationException
   *           if the bean model is not configured correctly
   */
  protected Field findIdField( final BeanModel model, final Object bean ) throws DataAccessException,
          ConfigurationException {
    Field result = null;
    final IdField idField = model.getBean().getIdField();
    if ( null != idField ) {
      final Bean declaringBean = idField.getDeclaringBean();
      final String declaringBeanType = declaringBean.getType();
      final String fieldName = idField.getName();
      final Class<?> beanClass = bean.getClass();
      final Class<?> declaringClass = DefaultBeanIdentificator.findAncestorClass( beanClass, declaringBeanType );
      if ( null == declaringClass ) {
        throw new ConfigurationException( DefaultBeanIdentificator.logger.get( "error.invalidDeclaringType",
                declaringBeanType, fieldName, beanClass.getName() ) );
      }
      try {
        result = declaringClass.getDeclaredField( fieldName );
        if ( !result.getType().isAssignableFrom( Identificator.class )
                && !String.class.isAssignableFrom( result.getType() ) ) {
          throw new ConfigurationException( DefaultBeanIdentificator.logger.get( "error.invalidFieldType",
                  declaringBeanType, fieldName, beanClass.getName(), Identificator.class.getName(),
                  String.class.getName() ) );
        }
      } catch ( final SecurityException e ) {
        throw new DataAccessException( DefaultBeanIdentificator.logger.get( "error.security.field", declaringBeanType,
                fieldName, beanClass.getName() ) );
      } catch ( final NoSuchFieldException e ) {
        throw new ConfigurationException( DefaultBeanIdentificator.logger.get( "error.invalidField", declaringBeanType,
                fieldName, beanClass.getName() ) );
      }
    }
    return result;
  }

  /**
   * Reads the application dependent identificator from the field.
   * 
   * @param field
   *          the field
   * @param bean
   *          the bean
   * @param create
   *          if <tt>true</tt>, then an identificator is created and assigned to
   *          the bean
   * @return the identificator or <tt>null</tt>, if no identificator is assigned
   *         and the <tt>create</tt> flag is <tt>false</tt>
   * @throws DataAccessException
   *           if reading the id field fails
   */
  protected ApplicationDependentIdentificator getIdentificator( final Field field, final Object bean,
          final boolean create ) throws DataAccessException {
    synchronized ( field ) {
      try {
        final boolean accessible = field.isAccessible();
        if ( !accessible ) {
          field.setAccessible( true );
        }
        try {
          ApplicationDependentIdentificator result = ( ApplicationDependentIdentificator ) field.get( bean );
          if ( null == result && create ) {
            result = new ApplicationDependentIdentificator();
            field.set( bean, result );
          }
          return result;
        } catch ( final IllegalAccessException e ) {
          throw new SecurityException( e );
        } finally {
          if ( !accessible ) {
            field.setAccessible( false );
          }
        }
      } catch ( final SecurityException e ) {
        throw new DataAccessException( DefaultBeanIdentificator.logger.get( "error.security.accessible",
                field.getDeclaringClass(), field.getName(), bean.getClass().getName() ) );
      }
    }
  }

  /**
   * Reads the identificator string from the field. This method is used when the
   * identificator field is of type {@link String}.
   * 
   * @param field
   *          the field
   * @param bean
   *          the bean
   * @return the identificator or <tt>null</tt>, if no identificator is assigned
   *         and the <tt>create</tt> flag is <tt>false</tt>
   * @throws DataAccessException
   *           if reading the id field fails
   */
  protected String getIdentificatorAsString( final Field field, final Object bean ) throws DataAccessException {
    synchronized ( field ) {
      try {
        final boolean accessible = field.isAccessible();
        if ( !accessible ) {
          field.setAccessible( true );
        }
        try {
          final String result = ( String ) field.get( bean );
          return result;
        } catch ( final IllegalAccessException e ) {
          throw new SecurityException( e );
        } finally {
          if ( !accessible ) {
            field.setAccessible( false );
          }
        }
      } catch ( final SecurityException e ) {
        throw new DataAccessException( DefaultBeanIdentificator.logger.get( "error.security.accessible",
                field.getDeclaringClass(), field.getName(), bean.getClass().getName() ) );
      }
    }
  }

  /**
   * Sets the identificator string to the field. This method is used when the
   * identificator field is of type {@link String}.
   * 
   * @param field
   *          the field
   * @param bean
   *          the bean
   * @param identificator
   *          the identificator or <tt>null</tt>
   * @throws DataAccessException
   *           if writing the id field fails
   */
  protected void setIdentificatorAsString( final Field field, final Object bean, final String identificator )
          throws DataAccessException {
    synchronized ( field ) {
      try {
        final boolean accessible = field.isAccessible();
        if ( !accessible ) {
          field.setAccessible( true );
        }
        try {
          field.set( bean, identificator );
        } catch ( final IllegalAccessException e ) {
          throw new SecurityException( e );
        } finally {
          if ( !accessible ) {
            field.setAccessible( false );
          }
        }
      } catch ( final SecurityException e ) {
        throw new DataAccessException( DefaultBeanIdentificator.logger.get( "error.security.accessible",
                field.getDeclaringClass(), field.getName(), bean.getClass().getName() ) );
      }
    }
  }

  @Override
  public Identificator getIdentificator( final BeanModel model, final Object bean ) throws DataAccessException,
          ConfigurationException {
    final Field field = findIdField( model, bean );
    final String application = model.getId().getApplicationId();
    if ( null != field ) {
      if ( String.class.isAssignableFrom( field.getType() ) ) {
        final String result = getIdentificatorAsString( field, bean );
        return new StringIdentificator( result );
      } else {
        final ApplicationDependentIdentificator id = getIdentificator( field, bean, false );
        if ( null != id ) {
          return id.get( application );
        }
      }
    }
    return null;
  }

  private static String toString( final Object o ) {
    return null != o ? o.toString() : null;
  }

  @Override
  public void setIdentificator( final BeanModel model, final Object bean, final Identificator identificator )
          throws DataAccessException, ConfigurationException {
    final Field field = findIdField( model, bean );
    final String application = model.getId().getApplicationId();
    if ( null != field ) {
      if ( String.class.isAssignableFrom( field.getType() ) ) {
        setIdentificatorAsString( field, bean,
                null != identificator ? DefaultBeanIdentificator.toString( identificator.getId( application ) ) : null );
      } else {
        final ApplicationDependentIdentificator id = getIdentificator( field, bean, true );
        id.set( application, identificator );
      }
    }
  }

}
