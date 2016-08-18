package de.ars.daojones.runtime.spi.beans.fields;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import de.ars.daojones.internal.runtime.beans.fields.DefaultConverter;
import de.ars.daojones.internal.runtime.configuration.beans.ModelElementHandler;
import de.ars.daojones.internal.runtime.configuration.beans.ModelElementHandler.ModelElementHandlingException;
import de.ars.daojones.internal.runtime.configuration.context.BeanModelHelper;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.internal.runtime.utilities.ReflectionHelper;
import de.ars.daojones.runtime.beans.fields.Converter;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.beans.fields.LoadContext;
import de.ars.daojones.runtime.beans.fields.Properties;
import de.ars.daojones.runtime.beans.fields.StoreContext;
import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.beans.identification.ApplicationDependentIdentificator;
import de.ars.daojones.runtime.beans.identification.BeanIdentificator;
import de.ars.daojones.runtime.beans.identification.DefaultBeanIdentificator;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.Constructor;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.configuration.beans.Field;
import de.ars.daojones.runtime.configuration.beans.GlobalConverter;
import de.ars.daojones.runtime.configuration.beans.IdField;
import de.ars.daojones.runtime.configuration.beans.InstanceProvidingElement;
import de.ars.daojones.runtime.configuration.beans.Invocable;
import de.ars.daojones.runtime.configuration.beans.LocalBeanIdentificator;
import de.ars.daojones.runtime.configuration.beans.LocalConverter;
import de.ars.daojones.runtime.configuration.beans.Method;
import de.ars.daojones.runtime.configuration.beans.MethodParameter;
import de.ars.daojones.runtime.configuration.beans.MethodResult;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.SelfDescribing;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * Default implementation that uses Java Reflection.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DefaultBeanAccessor implements BeanAccessor, BeanAccessorProvider {

  private static final Messages logger = Messages.create( "spi.beans.fields.Accessor" );

  @Override
  public Object getValue( final Object bean, final DatabaseFieldMappedElement fieldModel )
          throws FieldAccessException, ConfigurationException {
    final ThreadLocal<Object> result = new ThreadLocal<Object>();
    try {
      new ModelElementHandler() {

        @Override
        protected void handle( final MethodParameter model ) throws ConfigurationException {
          throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.read.parameter",
                  model.getFieldMapping().getName(), model.getDeclaringBean().getType() ) );
        }

        @Override
        protected void handle( final MethodResult model ) throws ConfigurationException, ModelElementHandlingException {
          // Invoke method

          final Invocable invocable = model.getInvocable();
          final Method methodModel = ( Method ) invocable;
          if ( !invocable.getParameters().isEmpty() ) {
            throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.read.method.parameters",
                    model.getFieldMapping().getName(), model.getDeclaringBean().getType(), methodModel.getName() ) );
          }
          // find method (Reflection API)
          final Class<?> declaringClass = ReflectionHelper.findClass( bean.getClass(),
                  methodModel.getDeclaringBean().getType() );
          if ( null == declaringClass ) {
            throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.read.method.invalidBeanType",
                    model.getFieldMapping().getName(), model.getDeclaringBean().getType(), methodModel.getName() ) );
          }
          try {
            final java.lang.reflect.Method method = BeanModelHelper.findMethod( declaringClass, methodModel );
            final Object methodResult = method.invoke( bean /*, args - currently not allowed*/ );
            result.set( methodResult );
          } catch ( final SecurityException e ) {
            throw new ConfigurationException( e );
          } catch ( final NoSuchMethodException e ) {
            throw new ConfigurationException( e );
          } catch ( final IllegalArgumentException e ) {
            throw new ConfigurationException( e );
          } catch ( final IllegalAccessException e ) {
            throw new ConfigurationException( e );
          } catch ( final InvocationTargetException e ) {
            throw new ModelElementHandler.ModelElementHandlingException( e );
          } catch ( final ClassNotFoundException e ) {
            throw new ConfigurationException( e );
          }
        }

        @Override
        protected void handle( final Field model ) throws ConfigurationException {
          // find field (Reflection API)
          final Class<?> declaringClass = ReflectionHelper.findClass( bean.getClass(),
                  model.getDeclaringBean().getType() );
          if ( null == declaringClass ) {
            throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.field.invalidBeanType",
                    model.getFieldMapping().getName(), model.getDeclaringBean().getType(), model.getName() ) );
          }
          try {
            final java.lang.reflect.Field field = declaringClass.getDeclaredField( model.getName() );
            final boolean accessible = field.isAccessible();
            synchronized ( field ) {
              try {
                if ( !accessible ) {
                  field.setAccessible( true );
                }
                final Object fieldResult = field.get( bean );
                result.set( fieldResult );
              } finally {
                if ( !accessible ) {
                  field.setAccessible( false );
                }
              }
            }
          } catch ( final SecurityException e ) {
            throw new ConfigurationException( e );
          } catch ( final NoSuchFieldException e ) {
            throw new ConfigurationException( e );
          } catch ( final IllegalArgumentException e ) {
            throw new ConfigurationException( e );
          } catch ( final IllegalAccessException e ) {
            throw new ConfigurationException( e );
          }
        }
      }.handle( fieldModel );
    } catch ( final ModelElementHandlingException e ) {
      throw new FieldAccessException( DefaultBeanAccessor.logger.get( "error.fieldaccess" ),
              fieldModel.getFieldMapping().getName(), e.getCause() );
    }
    return result.get();
  }

  @Override
  public void setValue( final Object bean, final DatabaseFieldMappedElement fieldModel, final Object value )
          throws FieldAccessException, ConfigurationException, ClassCastException {
    try {
      new ModelElementHandler() {

        @Override
        protected void handle( final MethodParameter model )
                throws ConfigurationException, ModelElementHandlingException {
          final Invocable invocable = model.getInvocable();
          if ( invocable instanceof Constructor ) {
            throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.write.method.constructor",
                    model.getFieldMapping().getName(), model.getDeclaringBean().getType() ) );
          }
          final Method methodModel = ( Method ) invocable;
          if ( invocable.getParameters().size() != 1 ) {
            throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.write.method.parameters",
                    model.getFieldMapping().getName(), model.getDeclaringBean().getType(), methodModel.getName() ) );
          }
          // find method (Reflection API)
          final Class<?> declaringClass = ReflectionHelper.findClass( bean.getClass(),
                  methodModel.getDeclaringBean().getType() );
          if ( null == declaringClass ) {
            throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.write.method.invalidBeanType",
                    model.getFieldMapping().getName(), model.getDeclaringBean().getType(), methodModel.getName() ) );
          }
          try {
            final java.lang.reflect.Method method = BeanModelHelper.findMethod( declaringClass, methodModel );
            final boolean accessible = method.isAccessible();
            if ( !accessible ) {
              method.setAccessible( true );
            }
            try {
              method.invoke( bean, value /*, further args - currently not allowed*/ );
            } finally {
              if ( !accessible ) {
                method.setAccessible( false );
              }
            }
          } catch ( final SecurityException e ) {
            throw new ConfigurationException( e );
          } catch ( final NoSuchMethodException e ) {
            throw new ConfigurationException( e );
          } catch ( final IllegalArgumentException e ) {
            throw new ConfigurationException( e );
          } catch ( final IllegalAccessException e ) {
            throw new ConfigurationException( e );
          } catch ( final InvocationTargetException e ) {
            throw new ModelElementHandler.ModelElementHandlingException( e );
          } catch ( final ClassNotFoundException e ) {
            throw new ConfigurationException( e );
          }
        }

        @Override
        protected void handle( final MethodResult model ) throws ConfigurationException {
          final Invocable invocable = model.getInvocable();
          final Method methodModel = ( Method ) invocable;
          throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.write.methodResult",
                  model.getFieldMapping().getName(), model.getDeclaringBean().getType(), methodModel.getName() ) );
        }

        @Override
        protected void handle( final Field model ) throws ConfigurationException {
          // find field (Reflection API)
          final Class<?> declaringClass = ReflectionHelper.findClass( bean.getClass(),
                  model.getDeclaringBean().getType() );
          if ( null == declaringClass ) {
            throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.write.field.invalidBeanType",
                    model.getFieldMapping().getName(), model.getDeclaringBean().getType(), model.getName() ) );
          }
          try {
            final java.lang.reflect.Field field = declaringClass.getDeclaredField( model.getName() );
            if ( Modifier.isFinal( field.getModifiers() ) ) {
              throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.write.field.final",
                      model.getFieldMapping().getName(), model.getDeclaringBean().getType(), model.getName() ) );
            }
            final boolean accessible = field.isAccessible();
            synchronized ( field ) {
              try {
                if ( !accessible ) {
                  field.setAccessible( true );
                }
                field.set( bean, value );
              } finally {
                if ( !accessible ) {
                  field.setAccessible( false );
                }
              }
            }
          } catch ( final SecurityException e ) {
            throw new ConfigurationException( e );
          } catch ( final NoSuchFieldException e ) {
            throw new ConfigurationException( e );
          } catch ( final IllegalArgumentException e ) {
            throw new ConfigurationException( e );
          } catch ( final IllegalAccessException e ) {
            throw new ConfigurationException( e );
          }
        }
      }.handle( fieldModel );
    } catch ( final ModelElementHandlingException e ) {
      throw new FieldAccessException( DefaultBeanAccessor.logger.get( "error.fieldaccess" ),
              fieldModel.getFieldMapping().getName(), e.getCause() );
    }
  }

  /**
   * Reads all values for method parameters from the database.
   *
   * @param context
   *          the field accessor context
   * @param parameters
   *          the parameter models
   * @param bean
   * @return the parameter values
   * @throws DataAccessException
   *           if accessing the database fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   * @throws FieldAccessException
   *           if converting the field value occured an error
   */
  protected Object[] getParameterValues( final BeanAccessorContext<?> context,
          final Collection<MethodParameter> parameters, final Object bean )
          throws DataAccessException, ConfigurationException, FieldAccessException {
    try {
      final Object[] result = new Object[parameters.size()];
      int idx = 0;
      // Read fields
      for ( final MethodParameter param : parameters ) {
        final Class<?> paramType = context.loadClass( param.getType() );
        result[idx] = readDatabaseFieldMapping( context, paramType, bean, param );
        idx++;
      }
      return result;
    } catch ( final ClassNotFoundException e ) {
      throw new ConfigurationException( e );
    }
  }

  /**
   * Reads a single database field mapping.
   *
   * @param <T>
   *          the field type
   * @param context
   *          the field accessor context
   * @param fieldType
   *          the field type class
   * @param beanObject
   *          the bean instance
   * @param element
   *          the field mapped element
   * @return the value
   * @throws DataAccessException
   *           if accessing the database fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   * @throws FieldAccessException
   *           if converting the field value occured an error
   */
  @SuppressWarnings( "unchecked" )
  protected <T> T readDatabaseFieldMappingRaw( final BeanAccessorContext<?> context, final Class<T> fieldType,
          final Object beanObject, final DatabaseFieldMappedElement element )
          throws DataAccessException, ConfigurationException, FieldAccessException {
    final Bean declaringBean = element.getDeclaringBean();
    T result = null;
    final DatabaseFieldMapping mapping = element.getFieldMapping();
    if ( null != mapping ) {
      final Converter converter = createConverter( context, fieldType, declaringBean, mapping );
      // only check for existing bean model
      getBeanModel( context, true );
      final LoadContext ctx = new LoadContext( this, context, beanObject, element, fieldType );
      // read field from database
      result = ( T ) converter.load( ctx );
    } // else parameter value is null
    // Check null values (esp. primitive types)
    if ( null == result ) {
      result = ReflectionHelper.getNullValue( fieldType );
    }
    // Return
    return result;
  }

  /**
   * Reads a single database field mapping.
   *
   * @param <T>
   *          the field type
   * @param context
   *          the field accessor context
   * @param fieldType
   *          the field type class
   * @param declaringBean
   *          the declaring bean model
   * @param beanObject
   *          the bean instance
   * @param element
   *          the field mapped element
   * @return the value
   * @throws DataAccessException
   *           if accessing the database fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   * @throws FieldAccessException
   *           if converting the field value occured an error
   */
  protected <T> void storeDatabaseFieldMapping( final BeanAccessorContext<?> context, final Class<T> fieldType,
          final Bean declaringBean, final Object beanObject, final DatabaseFieldMappedElement element, final T value )
          throws DataAccessException, ConfigurationException, FieldAccessException {
    final DatabaseFieldMapping mapping = element.getFieldMapping();
    if ( null != mapping ) {
      final Converter converter = createConverter( context, fieldType, declaringBean, mapping );
      // check for mandatory bean model
      getBeanModel( context, true );
      final StoreContext ctx = new StoreContext( this, context, beanObject, element, fieldType );
      // store field to the database
      converter.store( ctx, value );
    }
  }

  /**
   * Reads the bean model from the context. If the bean model is <tt>null</tt>
   * and the mandatory flag is set to <tt>true</tt>, a
   * {@link ConfigurationException} is thrown.
   *
   * @param context
   *          the context
   * @param mandatory
   *          the mandatory flag
   * @return the model
   * @throws ConfigurationException
   *           if the bean model is <tt>null</tt> and the mandatory flag is set
   *           to <tt>true</tt>
   */
  protected BeanModel getBeanModel( final BeanAccessorContext<?> context, final boolean mandatory )
          throws ConfigurationException {
    final BeanModel result = context.getModel();
    if ( null != result || !mandatory ) {
      return result;
    } else {
      throw new ConfigurationException(
              DefaultBeanAccessor.logger.get( "error.nobeanmodel", context.getBeanType().getName() ) );
    }
  }

  /**
   * Finds the bean model for a bean.
   *
   * @param ctx
   *          the application context
   * @param bean
   *          the bean
   * @return the bean model
   * @throws ConfigurationException
   *           if a bean model cannot be read in case of configuration errors
   */
  protected BeanModel getBeanModel( final ApplicationContext ctx, final Object bean, final boolean mandatory )
          throws ConfigurationException {
    final BeanAccessorContext<?> bac = BeanAccessorContext.createContextFor( ctx, bean.getClass(), null );
    return getBeanModel( bac, mandatory );
  }

  /**
   * Creates the converter instance for a given field.
   *
   * @param <T>
   *          the field type
   * @param context
   *          the context
   * @param fieldType
   *          the field type class
   * @param declaringBean
   *          the declaring bean
   * @param mapping
   *          the field mapping
   * @return the converter instance
   * @throws ConfigurationException
   */
  protected <T> Converter createConverter( final BeanAccessorContext<?> context, final Class<T> fieldType,
          final Bean declaringBean, final DatabaseFieldMapping mapping ) throws ConfigurationException {
    Converter result = null;
    // Create Converter and ConverterContext
    final BeanModel beanModel = getBeanModel( context, true );
    final String application = beanModel.getId().getApplicationId();
    final LocalConverter localConverter = mapping.getConverter();
    if ( null != localConverter && !DefaultConverter.class.getName()
            .equals( localConverter.getInstanceProvider().getInstanceClassName() ) ) {
      // field specific converter
      result = getProvidedInstance( context, localConverter );
    } else {
      result = findGlobalConverter( context, fieldType, application );
    }
    if ( null == result ) {
      result = new DefaultConverter();
    }
    return result;
  }

  protected <T> T getProvidedInstance( final BeanAccessorContext<?> context, final InstanceProvidingElement<T> model )
          throws ConfigurationException {
    return getProvidedInstance( context.getBeanType(), model );
  }

  protected <T> T getProvidedInstance( final Class<?> beanType, final InstanceProvidingElement<T> model )
          throws ConfigurationException {
    return model.getInstance( beanType.getClassLoader() );
  }

  protected <T> Converter findGlobalConverter( final BeanAccessorContext<?> context, final Class<T> fieldType,
          final String application ) throws ConfigurationException {
    Converter result = null;
    final GlobalConverter globalConverter = context.getBeanModelManager().getConverterModelManager()
            .findConverterFor( application, fieldType );
    if ( null != globalConverter ) {
      result = getProvidedInstance( context, globalConverter );
    } else {
      // If the field type is an array, check for single object converter and create array type converter
      if ( fieldType.isArray() ) {
        result = findGlobalConverter( context, fieldType.getComponentType(), application );
      }
    }
    return result;
  }

  /**
   * Reads the database id.
   *
   * @param <T>
   *          the field type
   * @param context
   *          the field accessor context
   * @param fieldType
   *          the field type class
   * @param declaringBean
   *          the declaring bean model
   * @param beanObject
   *          the bean instance
   * @param element
   *          the field mapped element
   * @param identificator
   *          the identificator to be injected
   * @return the value
   * @throws DataAccessException
   *           if accessing the database fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   * @throws FieldAccessException
   *           if converting the field value occured an error
   */
  @SuppressWarnings( "unchecked" )
  protected <T> T readDatabaseId( final BeanAccessorContext<?> context, final Class<T> fieldType,
          final Bean declaringBean, final Object beanObject, final IdField element, final Identificator identificator )
          throws DataAccessException, ConfigurationException, FieldAccessException {
    if ( fieldType.isAssignableFrom( Identificator.class ) ) {
      final ApplicationDependentIdentificator adi = new ApplicationDependentIdentificator();
      final BeanModel beanModel = getBeanModel( context, true );
      adi.set( beanModel.getId().getApplicationId(), identificator );
      return ( T ) adi;
      //    } else if ( fieldType.isAssignableFrom( Serializable.class ) ) {
      //      return ( T ) identificator.getId( beanModel.getId().getApplicationId() );
    }
    if ( fieldType.isAssignableFrom( String.class ) ) {
      final BeanModel beanModel = getBeanModel( context, true );
      return ( T ) identificator.getId( beanModel.getId().getApplicationId() ).toString();
    }
    throw new FieldAccessException( DefaultBeanAccessor.logger.get( "error.write.field.id.invalidType",
            element.getName(), declaringBean.getType(), Identificator.class ), element.getName() );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public <T> T createBeanInstance( final BeanAccessorContext<T> context )
          throws FieldAccessException, ConfigurationException, DataAccessException {
    try {
      try {
        final Identificator identificator = context.getDb().getIdentificator();
        final BeanModel beanModel = getBeanModel( context, false );
        final T result;
        final Class<T> beanType = context.getBeanType();
        if ( null != beanModel ) {
          // no reservation, only check!
          checkInjectionKey( context, identificator );
          // Create instance
          // Constructor injection
          final Constructor constructor = beanModel.getBean().getConstructor();
          if ( null != constructor ) {
            final java.lang.reflect.Constructor<? extends T> c = BeanModelHelper.findConstructor( beanType,
                    constructor );
            final Object[] params = getParameterValues( context, constructor.getParameters(), null );
            result = c.newInstance( params );
          } else {
            result = ReflectionHelper.newInstance( beanType );
          }
          final InjectionKey injectionKey = getInjectionKey( context, result, identificator );
          try {
            //Injection to other fields
            this.injectWithKey( context, result, injectionKey, true );
          } finally {
            injectionKey.release();
          }
        } else {
          DefaultBeanAccessor.logger.log( Level.WARNING, "error.nobeanmodel", beanType.getName() );
          result = ReflectionHelper.newInstance( beanType );
        }
        return result;
      } catch ( final AlreadyInjectingException e ) {
        return ( T ) e.getBean();
      }
    } catch ( final SecurityException e ) {
      throw new ConfigurationException( e );
    } catch ( final IllegalArgumentException e ) {
      throw new ConfigurationException( e );
    } catch ( final ClassNotFoundException e ) {
      throw new ConfigurationException( e );
    } catch ( final NoSuchMethodException e ) {
      throw new ConfigurationException( e );
    } catch ( final InstantiationException e ) {
      throw new ConfigurationException( e );
    } catch ( final IllegalAccessException e ) {
      throw new ConfigurationException( e );
    } catch ( final InvocationTargetException e ) {
      throw new ConfigurationException( e );
    }
  }

  @SuppressWarnings( "unchecked" )
  protected <T> void storeField( final BeanAccessorContext<T> context, final T bean, final Field field )
          throws FieldAccessException, DataAccessException, ConfigurationException {
    if ( field.getFieldMapping().getUpdatePolicy() != UpdatePolicy.NEVER ) {
      // Find field type
      final java.lang.reflect.Field reflField = findField( context, field.getDeclaringBean(), field.getName() );
      // Read the value from the field
      final Object value = readValueFromField( context, reflField, bean );
      storeDatabaseFieldMapping( context, ( Class<Object> ) reflField.getType(), field.getDeclaringBean(), bean, field,
              value );
    }
  }

  protected <T> void injectField( final BeanAccessorContext<T> context, final T bean, final Field field )
          throws FieldAccessException, DataAccessException, ConfigurationException {
    // Find field type
    final java.lang.reflect.Field reflField = findField( context, field.getDeclaringBean(), field.getName() );
    final Object value = readDatabaseFieldMapping( context, reflField.getType(), bean, field );
    // Assign the value to the field
    setValueToField( context, reflField, bean, value );
  }

  @SuppressWarnings( "unchecked" )
  protected <T> T readDatabaseFieldMapping( final BeanAccessorContext<?> context, final Class<T> fieldType,
          final Object beanObject, final DatabaseFieldMappedElement model )
          throws DataAccessException, ConfigurationException, FieldAccessException {
    final DatabaseFieldMapping fieldMapping = model.getFieldMapping();
    if ( null != fieldMapping ) {
      final UpdatePolicy updatePolicy = fieldMapping.getUpdatePolicy();
      Object value;
      switch ( updatePolicy ) {
      case REPLACE:
      case NEVER:
        value = readDatabaseFieldMappingRaw( context, fieldType, beanObject, model );
        break;
      case APPEND:
      case INSERT:
        value = null;
        break;
      default:
        throw new IllegalArgumentException( "" + updatePolicy );
      }
      if ( null == value ) {
        // Primitive types!!!
        value = ReflectionHelper.getNullValue( fieldType );
      }
      return ( T ) value;
    } else {
      throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.read.missingFieldMapping", model ) );
    }
  }

  protected <T> void injectIdField( final BeanAccessorContext<T> context, final T bean, final IdField field,
          final Identificator identificator ) throws FieldAccessException, DataAccessException, ConfigurationException {
    // Find field type
    final java.lang.reflect.Field reflField = findField( context, field.getDeclaringBean(), field.getName() );
    final Object value = readDatabaseId( context, reflField.getType(), field.getDeclaringBean(), bean, field,
            identificator );
    // Assign the value to the field
    setValueToField( context, reflField, bean, value );
  }

  /**
   * Assigns a value to a Java bean's field.
   *
   * @param model
   *          the field model
   * @param bean
   *          the bean
   * @param value
   *          the value
   * @throws ConfigurationException
   */
  protected <T> void setValueToField( final BeanAccessorContext<T> context, final java.lang.reflect.Field field,
          final T bean, final Object value ) throws ConfigurationException {
    try {
      if ( Modifier.isFinal( field.getModifiers() ) ) {
        throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.write.field.final", field.getName(),
                field.getDeclaringClass().getName() ) );
      }
      final boolean accessible = field.isAccessible();
      synchronized ( field ) {
        try {
          if ( !accessible ) {
            field.setAccessible( true );
          }
          field.set( bean, value );
        } finally {
          if ( !accessible ) {
            field.setAccessible( false );
          }
        }
      }
    } catch ( final SecurityException e ) {
      throw new ConfigurationException( e );
    } catch ( final IllegalArgumentException e ) {
      throw new ConfigurationException( e );
    } catch ( final IllegalAccessException e ) {
      throw new ConfigurationException( e );
    }
  }

  /**
   * Reads a value from a Java bean's field.
   *
   * @param model
   *          the field model
   * @param bean
   *          the bean
   * @return the value
   * @throws ConfigurationException
   */
  protected <T> Object readValueFromField( final BeanAccessorContext<T> context, final java.lang.reflect.Field field,
          final T bean ) throws ConfigurationException {
    try {
      final boolean accessible = field.isAccessible();
      synchronized ( field ) {
        try {
          if ( !accessible ) {
            field.setAccessible( true );
          }
          return field.get( bean );
        } finally {
          if ( !accessible ) {
            field.setAccessible( false );
          }
        }
      }
    } catch ( final SecurityException e ) {
      throw new ConfigurationException( e );
    } catch ( final IllegalArgumentException e ) {
      throw new ConfigurationException( e );
    } catch ( final IllegalAccessException e ) {
      throw new ConfigurationException( e );
    }
  }

  /**
   * Assigns a value to a Java bean's field.
   *
   * @param model
   *          the field model
   * @param bean
   *          the bean
   * @param value
   *          the value
   * @throws ConfigurationException
   */
  protected <T> void setValueToField( final BeanAccessorContext<T> context, final Field model, final T bean,
          final Object value ) throws ConfigurationException {
    final java.lang.reflect.Field field = findField( context, model.getDeclaringBean(), model.getName() );
    setValueToField( context, field, bean, value );
  }

  private <T> java.lang.reflect.Field findField( final BeanAccessorContext<T> context, final Bean bean,
          final String name ) throws ConfigurationException {
    // find field (Reflection API)
    try {
      final Class<?> declaringClass = context.loadClass( bean );
      final java.lang.reflect.Field result = declaringClass.getDeclaredField( name );
      return result;
    } catch ( final SecurityException e ) {
      throw new ConfigurationException( e );
    } catch ( final ClassNotFoundException e ) {
      throw new ConfigurationException( e );
    } catch ( final NoSuchFieldException e ) {
      throw new ConfigurationException( e );
    }
  }

  private <T> java.lang.reflect.Method findMethod( final BeanAccessorContext<T> context, final Method model )
          throws ConfigurationException {
    // find field (Reflection API)
    try {
      final Class<?> declaringClass = context.loadClass( model.getDeclaringBean() );
      final java.lang.reflect.Method result = BeanModelHelper.findMethod( declaringClass, model );
      return result;
    } catch ( final SecurityException e ) {
      throw new ConfigurationException( e );
    } catch ( final ClassNotFoundException e ) {
      throw new ConfigurationException( e );
    } catch ( final NoSuchMethodException e ) {
      throw new ConfigurationException( e );
    }
  }

  @SuppressWarnings( "unchecked" )
  protected <T> void storeMethod( final BeanAccessorContext<T> context, final T bean, final Method method )
          throws FieldAccessException, DataAccessException, ConfigurationException {
    try {
      final MethodResult methodResult = method.getResult();
      if ( null != methodResult ) {
        if ( methodResult.getFieldMapping().getUpdatePolicy() != UpdatePolicy.NEVER ) {
          final java.lang.reflect.Method reflMethod = findMethod( context, method );
          final boolean accessible = reflMethod.isAccessible();
          if ( !accessible ) {
            reflMethod.setAccessible( true );
          }
          try {
            final Object[] params = getParameterValues( context, method.getParameters(), bean );
            final Object result = reflMethod.invoke( bean, params );
            storeDatabaseFieldMapping( context, ( Class<Object> ) reflMethod.getReturnType(),
                    context.getModel().getBean(), bean, methodResult, result );
          } finally {
            if ( !accessible ) {
              reflMethod.setAccessible( false );
            }
          }
        }
      }
    } catch ( final IllegalArgumentException e ) {
      throw new ConfigurationException( e );
    } catch ( final IllegalAccessException e ) {
      throw new ConfigurationException( e );
    } catch ( final InvocationTargetException e ) {
      throw new ConfigurationException( e );
    }
  }

  protected boolean isParametersForInjection( final List<MethodParameter> parameters ) {
    if ( null != parameters ) {
      for ( final MethodParameter param : parameters ) {
        if ( null != param.getFieldMapping() ) {
          return true;
        }
      }
    }
    return false;
  }

  protected <T> void injectMethod( final BeanAccessorContext<T> context, final T bean, final Method method )
          throws FieldAccessException, DataAccessException, ConfigurationException {
    try {
      final java.lang.reflect.Method reflMethod = findMethod( context, method );
      final List<MethodParameter> parameters = method.getParameters();
      if ( isParametersForInjection( parameters ) ) {
        final Object[] params = getParameterValues( context, parameters, bean );
        reflMethod.invoke( bean, params );
      }
    } catch ( final IllegalArgumentException e ) {
      throw new ConfigurationException( e );
    } catch ( final IllegalAccessException e ) {
      throw new ConfigurationException( e );
    } catch ( final InvocationTargetException e ) {
      throw new ConfigurationException( e );
    }
  }

  private static final ThreadLocal<Map<InjectionKey, Object>> alreadyInjectedBeans = new ThreadLocal<Map<InjectionKey, Object>>();

  private static final class InjectionKey {
    private final String application;
    private final String beanType;
    private final Identificator identificator;
    private final boolean threadFirst;

    public InjectionKey( final BeanModel beanModel, final Identificator identificator, final boolean threadFirst ) {
      super();
      application = beanModel.getId().getApplicationId();
      beanType = beanModel.getId().getTypeId();
      this.identificator = identificator;
      this.threadFirst = threadFirst;
    }

    /**
     * @return the threadFirst
     */
    public boolean isThreadFirst() {
      return threadFirst;
    }

    /**
     * @return the identificator
     */
    public Identificator getIdentificator() {
      return identificator;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( application == null ) ? 0 : application.hashCode() );
      result = prime * result + ( ( beanType == null ) ? 0 : beanType.hashCode() );
      result = prime * result + ( ( identificator == null ) ? 0 : identificator.hashCode() );
      return result;
    }

    public void release() {
      DefaultBeanAccessor.alreadyInjectedBeans.get().remove( this );
      if ( isThreadFirst() ) {
        DefaultBeanAccessor.alreadyInjectedBeans.remove();
      }
    }

    @Override
    public boolean equals( final Object obj ) {
      if ( this == obj ) {
        return true;
      }
      if ( obj == null ) {
        return false;
      }
      if ( getClass() != obj.getClass() ) {
        return false;
      }
      final InjectionKey other = ( InjectionKey ) obj;
      if ( application == null ) {
        if ( other.application != null ) {
          return false;
        }
      } else if ( !application.equals( other.application ) ) {
        return false;
      }
      if ( beanType == null ) {
        if ( other.beanType != null ) {
          return false;
        }
      } else if ( !beanType.equals( other.beanType ) ) {
        return false;
      }
      if ( identificator == null ) {
        if ( other.identificator != null ) {
          return false;
        }
      } else if ( !identificator.equals( other.identificator ) ) {
        return false;
      }
      return true;
    }

  }

  private void checkInjectionKey( final BeanAccessorContext<?> context, final Identificator identificator )
          throws AlreadyInjectingException, DataAccessException, ConfigurationException {
    final BeanModel beanModel = getBeanModel( context, true );
    // Check for cycle
    final Map<InjectionKey, Object> beans = DefaultBeanAccessor.alreadyInjectedBeans.get();
    // synchronization is not necessary because of ThreadLocal usage
    final boolean threadFirst = null == beans;
    if ( !threadFirst ) {
      final InjectionKey key = new InjectionKey( beanModel, identificator, false );
      if ( beans.containsKey( key ) ) {
        final Object alreadyInjectingBean = beans.get( key );
        throw new AlreadyInjectingException( alreadyInjectingBean );
      }
    }
  }

  private <T> InjectionKey getInjectionKey( final BeanAccessorContext<T> context, final T bean,
          final Identificator identificator )
          throws AlreadyInjectingException, DataAccessException, ConfigurationException {
    final BeanModel beanModel = getBeanModel( context, true );
    // Check for cycle
    Map<InjectionKey, Object> beans = DefaultBeanAccessor.alreadyInjectedBeans.get();
    // synchronization is not necessary because of ThreadLocal usage
    final boolean threadFirst = null == beans;
    if ( threadFirst ) {
      beans = new HashMap<InjectionKey, Object>();
      DefaultBeanAccessor.alreadyInjectedBeans.set( beans );
    }
    // synchronization is not necessary because of ThreadLocal usage
    final InjectionKey key = new InjectionKey( beanModel, identificator, threadFirst );
    if ( beans.containsKey( key ) ) {
      final Object alreadyInjectingBean = beans.get( key );
      throw new AlreadyInjectingException( alreadyInjectingBean );
    } else {
      beans.put( key, bean );
      return key;
    }
  }

  @Override
  public <T> void inject( final BeanAccessorContext<T> context, final T bean )
          throws FieldAccessException, DataAccessException, ConfigurationException, AlreadyInjectingException {
    final Identificator identificator = context.getDb().getIdentificator();
    final InjectionKey injectionKey = getInjectionKey( context, bean, identificator );
    try {
      injectWithKey( context, bean, injectionKey, true );
    } finally {
      injectionKey.release();
    }
  }

  @Override
  public <T> void reinjectAfterStore( final BeanAccessorContext<T> context, final T bean )
          throws FieldAccessException, DataAccessException, ConfigurationException, AlreadyInjectingException {
    final Identificator identificator = context.getDb().getIdentificator();
    final InjectionKey injectionKey = getInjectionKey( context, bean, identificator );
    try {
      injectWithKey( context, bean, injectionKey, false );
    } finally {
      injectionKey.release();
    }
  }

  private <T> void injectWithKey( final BeanAccessorContext<T> context, final T bean, final InjectionKey injectionKey,
          final boolean full ) throws FieldAccessException, DataAccessException, ConfigurationException {
    // full --> inject all
    // !full --> inject only identificator and fields that are computed or appended/inserted (after storing or deleting a bean)
    final BeanModel beanModel = getBeanModel( context, true );
    final Identificator identificator = injectionKey.getIdentificator();
    // Check for cycle
    final Bean model = beanModel.getBean();
    setIdentificator( beanModel, bean, identificator );
    for ( final Field field : model.getFields() ) {
      final DatabaseFieldMapping fm = field.getFieldMapping();
      // Inject computed fields
      boolean doInjection = full || Properties.isComputed( fm.getMetadata() );
      if ( !doInjection ) {
        switch ( fm.getUpdatePolicy() ) {
        case APPEND:
        case INSERT:
          // inject fields that are appended or inserted
          doInjection = true;
          break;
        case REPLACE:
        case NEVER:
          // nothing to do
          break;
        }
      }
      if ( doInjection ) {
        injectField( context, bean, field );
      }
    }
    if ( full ) {
      // inject method parameters
      for ( final Method method : model.getMethods() ) {
        injectMethod( context, bean, method );
      }
    }
  }

  @Override
  public <T> void store( final BeanAccessorContext<T> context, final T bean )
          throws FieldAccessException, DataAccessException, ConfigurationException {
    final BeanModel beanModel = getBeanModel( context, true );
    // Check for cycle
    final Bean model = beanModel.getBean();
    // store further fields
    for ( final Field field : model.getFields() ) {
      storeField( context, bean, field );
    }
    // store method parameters
    for ( final Method method : model.getMethods() ) {
      storeMethod( context, bean, method );
    }
  }

  private abstract class SimpleDatabaseAccessor implements DatabaseAccessor {

    private final Map<String, Object> fieldValues = new HashMap<String, Object>();

    public void setFieldValue( final String name, final Object value ) {
      fieldValues.put( name, value );
    }

    public Object getFieldValue( final String name ) {
      return fieldValues.get( name );
    }

    @Override
    public <E> void setFieldValue( final FieldContext<E> context, final E value )
            throws DataAccessException, UnsupportedFieldTypeException {
      setFieldValue( context.getName(), value );
    }

    @Override
    public String[] getFields() throws DataAccessException {
      final Set<String> keySet = fieldValues.keySet();
      return keySet.toArray( new String[keySet.size()] );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <E> E getFieldValue( final FieldContext<E> context )
            throws DataAccessException, UnsupportedFieldTypeException {
      return ( E ) fieldValues.get( context.getName() );
    }

  }

  protected DatabaseFieldMappedElement findFieldModel( final BeanModel beanModel, final Object bean,
          final String field ) throws ConfigurationException {
    final DatabaseFieldMappedElement fieldModel;
    if ( bean instanceof SelfDescribing ) {
      fieldModel = ( ( SelfDescribing ) bean ).findFieldModel( field, false );
    } else {
      fieldModel = BeanModelHelper.findFieldModel( beanModel.getBean(), field, false );
    }
    if ( null == fieldModel ) {
      throw new ConfigurationException( DefaultBeanAccessor.logger.get( "error.nofieldmodel",
              beanModel.getId().getApplicationId(), bean.getClass().getName(), field ) );
    }
    return fieldModel;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public Object getDatabaseValue( final ApplicationContext ctx, final Object bean, final String field )
          throws FieldAccessException, ConfigurationException {
    boolean handled = false;
    final Class<Object> beanType = ( Class<Object> ) bean.getClass();
    final BeanModelManager beanModelManager = ctx.getBeanModelManager();
    final BeanModel beanModel = getBeanModel( ctx, bean, true );
    final ConnectionProvider cp = ctx.getConnectionProvider();
    final SimpleDatabaseAccessor db = new SimpleDatabaseAccessor() {

      @Override
      public Identificator getIdentificator() throws DataAccessException {
        try {
          return DefaultBeanAccessor.this.getIdentificator( beanModel, bean );
        } catch ( final ConfigurationException e ) {
          throw new DataAccessException( e );
        }
      }

    };
    final BeanAccessorContext<Object> context = new BeanAccessorContext<Object>( beanType, beanModel, cp, db,
            beanModelManager );
    final DatabaseFieldMappedElement fieldModel = findFieldModel( beanModel, bean, field );
    try {
      if ( fieldModel instanceof Field ) {
        // use the original store method
        storeField( context, bean, ( Field ) fieldModel );
        handled = true;
      } else if ( fieldModel instanceof MethodResult ) {
        final Invocable invocable = ( ( MethodResult ) fieldModel ).getInvocable();
        if ( invocable instanceof Method ) {
          // use the original store method
          storeMethod( context, bean, ( Method ) invocable );
          handled = true;
        }
      }
      if ( handled ) {
        return db.getFieldValue( fieldModel.getFieldMapping().getName() );
      } else {
        throw new FieldAccessException( DefaultBeanAccessor.logger.get( "error.fieldaccess" ),
                fieldModel.getFieldMapping().getName() );
      }
    } catch ( final DataAccessException e ) {
      throw new FieldAccessException( fieldModel.getFieldMapping().getName(), e );
    }
  }

  /* *******************************************
   *    B E A N   I D E N T I F I C A T O R    *
   * ***************************************** */

  private BeanIdentificator getBeanIdentificator( final Bean model, final Class<?> beanClass )
          throws ConfigurationException {
    BeanIdentificator result = null;
    final LocalBeanIdentificator localBeanIdentificator = model.getIdentificator();
    if ( null != localBeanIdentificator ) {
      result = getProvidedInstance( beanClass, localBeanIdentificator );
    }
    if ( null == result ) {
      result = new DefaultBeanIdentificator();
    }
    return result;
  }

  @Override
  public Identificator getIdentificator( final BeanModel model, final Object bean )
          throws DataAccessException, ConfigurationException {
    return getBeanIdentificator( model.getBean(), bean.getClass() ).getIdentificator( model, bean );
  }

  @Override
  public void setIdentificator( final BeanModel model, final Object bean, final Identificator identificator )
          throws DataAccessException, ConfigurationException {
    getBeanIdentificator( model.getBean(), bean.getClass() ).setIdentificator( model, bean, identificator );
  }

  @Override
  public BeanAccessor getBeanAccessor() {
    return this;
  }

}
