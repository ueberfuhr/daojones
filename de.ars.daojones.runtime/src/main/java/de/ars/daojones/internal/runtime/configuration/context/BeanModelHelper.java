package de.ars.daojones.internal.runtime.configuration.context;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import de.ars.daojones.internal.runtime.utilities.Configuration;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.internal.runtime.utilities.ReflectionHelper;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.Constructor;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping;
import de.ars.daojones.runtime.configuration.beans.Field;
import de.ars.daojones.runtime.configuration.beans.Method;
import de.ars.daojones.runtime.configuration.beans.MethodParameter;
import de.ars.daojones.runtime.configuration.beans.MethodResult;
import de.ars.daojones.runtime.configuration.beans.TypeMappedElement;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * Helper class providing utility methods for the bean model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class BeanModelHelper {

  private static final Messages bundle = Messages.create( "configuration.context.BeanModelHelper" );

  private BeanModelHelper() {
    super();
  }

  /**
   * Resolves the database name of the field with a given id.<br/>
   * <br/>
   * <b>Please note:</b> If the id could not be found, the id is resolved to be
   * the database name of the field by default. This behaviour can be switched
   * to throw a ConfigurationException by setting the system property
   * <tt>{@value Configuration#RESOLVE_FIELDS_TO_NAME_PROPERTY}</tt> to
   * <tt>true</tt>.
   * 
   * @param bean
   *          the bean model
   * @param id
   *          the id of the field
   * @return the name of the database field
   * @throws ConfigurationException
   *           if there isn't any field with the given id.
   */
  public static String resolveFieldName( final Bean bean, final String id ) throws ConfigurationException {
    final DatabaseFieldMappedElement fieldModel = BeanModelHelper.findFieldModel( bean, id );
    if ( null != fieldModel ) {
      return fieldModel.getFieldMapping().getName();
    } else {
      if ( Configuration.isResolveFieldsToName() ) {
        BeanModelHelper.bundle.log( Level.WARNING, "warning.missing.field", bean.getType(), id );
        return id;
      } else {
        throw new ConfigurationException( BeanModelHelper.bundle.get( "error.missing.field", bean.getType(), id ) );
      }
    }
  }

  /**
   * Finds a field mapped element.
   * 
   * @param bean
   *          the bean
   * @param id
   *          the id of the field
   * @param write
   *          a flag to find readable (<tt>false</tt>) or writable (
   *          <tt>true</tt>) fields
   * @return the field mapped element or <tt>null</tt>
   */
  public static DatabaseFieldMappedElement findFieldModel( final Bean bean, final String id, final boolean write ) {
    return BeanModelHelper.findFieldModel( bean, id, write, !write );
  }

  /**
   * Finds a field mapped element. If there are multiple elements (one for
   * reading and one for writing), the first occurence is returned.
   * 
   * @param bean
   *          the bean
   * @param id
   *          the id of the field
   * @return the field mapped element or <tt>null</tt>
   */
  public static DatabaseFieldMappedElement findFieldModel( final Bean bean, final String id ) {
    return BeanModelHelper.findFieldModel( bean, id, true, true );
  }

  public static DatabaseFieldMappedElement findFieldModel( final Bean bean, final String id, final boolean write,
          final boolean read ) {
    if ( null == id || "".equals( id ) ) {
      return null;
    } else {
      final boolean resolveFieldsToName = Configuration.isResolveFieldsToName();
      DatabaseFieldMappedElement candidatePerName = null;
      for ( final Field field : bean.getFields() ) {
        if ( id.equals( field.getFieldMapping().getId() ) ) {
          return field;
        } else if ( resolveFieldsToName && null == candidatePerName && id.equals( field.getFieldMapping().getName() ) ) {
          candidatePerName = field;
        }
      }
      if ( write ) {
        // Use a list because method parameters should be found before constructor parameters
        final List<MethodParameter> parameters = new LinkedList<MethodParameter>();
        for ( final Method method : bean.getMethods() ) {
          parameters.addAll( method.getParameters() );
        }
        final Constructor constructor = bean.getConstructor();
        if ( null != constructor ) {
          parameters.addAll( constructor.getParameters() );
        }
        for ( final MethodParameter param : parameters ) {
          final DatabaseFieldMapping fm = param.getFieldMapping();
          if ( null != fm && id.equals( fm.getId() ) ) {
            return param;
          } else if ( resolveFieldsToName && null == candidatePerName && null != id && null != fm
                  && id.equals( fm.getName() ) ) {
            candidatePerName = param;
          }
        }
      }
      if ( read ) {
        for ( final Method method : bean.getMethods() ) {
          final MethodResult result = method.getResult();
          if ( null != result ) {
            final DatabaseFieldMapping fm = result.getFieldMapping();
            if ( null != fm && id.equals( fm.getId() ) ) {
              return result;
            } else if ( resolveFieldsToName && null == candidatePerName && id.equals( fm.getName() ) ) {
              candidatePerName = result;
            }
          }
        }
      }
      if ( resolveFieldsToName && null != candidatePerName ) {
        BeanModelHelper.bundle.log( Level.WARNING, "warning.missing.field", bean.getType(), id );
        return candidatePerName;
      }
    }
    return null;
  }

  public static java.lang.reflect.Method findMethod( final Class<?> declaringClass, final Method method )
          throws ClassNotFoundException, SecurityException, NoSuchMethodException {
    final List<MethodParameter> parameters = method.getParameters();
    final Class<?>[] parameterTypes = BeanModelHelper.getTypes( parameters, declaringClass.getClassLoader() );
    return declaringClass.getDeclaredMethod( method.getName(), parameterTypes );
  }

  public static <T> java.lang.reflect.Constructor<T> findConstructor( final Class<T> declaringClass,
          final Constructor constructor ) throws ClassNotFoundException, SecurityException, NoSuchMethodException {
    final List<MethodParameter> parameters = constructor.getParameters();
    final Class<?>[] parameterTypes = BeanModelHelper.getTypes( parameters, declaringClass.getClassLoader() );
    return declaringClass.getConstructor( parameterTypes );
  }

  public static String getPackage( final TypeMappedElement element ) {
    final String type = element.getType();
    return type.substring( 0, Math.min( type.lastIndexOf( '.' ), 0 ) );
  }

  public static Class<?>[] getTypes( final List<MethodParameter> parameters, final ClassLoader cl )
          throws ClassNotFoundException {
    final Class<?>[] parameterTypes = new Class<?>[parameters.size()];
    int idx = 0;
    for ( final MethodParameter param : parameters ) {
      parameterTypes[idx] = ReflectionHelper.findClass( param.getType(), cl );
      idx++;
    }
    return parameterTypes;
  }

}
