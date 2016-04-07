package de.ars.daojones.internal.runtime.configuration.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import de.ars.daojones.internal.runtime.beans.fields.DefaultConverter;
import de.ars.daojones.internal.runtime.configuration.provider.MethodParametersAnnotationsScanner.Parameter;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.internal.runtime.utilities.ReflectionsHelper;
import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.beans.annotations.FieldRef;
import de.ars.daojones.runtime.beans.annotations.Id;
import de.ars.daojones.runtime.beans.fields.Converter;
import de.ars.daojones.runtime.beans.fields.Converts;
import de.ars.daojones.runtime.beans.identification.BeanIdentificator;
import de.ars.daojones.runtime.beans.identification.DefaultBeanIdentificator;
import de.ars.daojones.runtime.beans.identification.IdentifiedBy;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.BeanConfiguration;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappingReference;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping;
import de.ars.daojones.runtime.configuration.beans.GlobalConverter;
import de.ars.daojones.runtime.configuration.beans.IdField;
import de.ars.daojones.runtime.configuration.beans.InstanceProvidingElement.InstanceByClassProvider;
import de.ars.daojones.runtime.configuration.beans.LocalBeanIdentificator;
import de.ars.daojones.runtime.configuration.beans.LocalConverter;
import de.ars.daojones.runtime.configuration.beans.MethodParameter;
import de.ars.daojones.runtime.configuration.beans.MethodResult;
import de.ars.daojones.runtime.configuration.beans.ObjectFactory;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationProvider;
import de.ars.daojones.runtime.configuration.provider.FieldAnnotationHandler;

/**
 * Avoid loading this class in a static way, because it depends from the Google
 * Reflections API.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class AnnotationBeanConfigurationProviderForReflections implements ConfigurationProvider<BeanConfiguration> {

  private static final Messages logger = Messages.create( "beans.model.annotations.Scanner" );
  private static final Level level = Level.FINER;

  private final ClassLoader dependenciesClassLoader;
  private final URL[] resourceBases;
  private final Set<String> fieldIds = new HashSet<String>();
  private final Set<String> fieldRefIds = new HashSet<String>();
  private Reflections reflections;

  public AnnotationBeanConfigurationProviderForReflections( final ClassLoader dependenciesClassLoader,
          final URL[] resourceBases ) {
    super();
    this.dependenciesClassLoader = dependenciesClassLoader;
    this.resourceBases = resourceBases;
  }

  /**
   * Returns the Reflections instance. This method initializes the reflections
   * lazily and caches it into an internal field. Calling this method multiple
   * times will result in the same Reflections instance.
   *
   * @return the Reflections instance
   */
  protected final Reflections getReflections() {
    synchronized ( this ) {
      if ( null == reflections ) {
        reflections = ReflectionsHelper.createReflections( resourceBases, dependenciesClassLoader,
                new FieldAnnotationsScanner(), new MethodAnnotationsScanner(), new MethodParametersAnnotationsScanner(),
                new TypeAnnotationsScanner() );
      }
      return reflections;
    }
  }

  @Override
  public Iterable<BeanConfiguration> readConfiguration() throws ConfigurationException {
    if ( AnnotationBeanConfigurationProviderForReflections.logger
            .isLoggable( AnnotationBeanConfigurationProviderForReflections.level ) ) {
      AnnotationBeanConfigurationProviderForReflections.logger.log(
              AnnotationBeanConfigurationProviderForReflections.level, "scan.start",
              BeanConfiguration.class.getSimpleName() );
    }
    // do not replace usage of Reflections by java.util.reflection
    // loading all classes in classpath immediately is slow
    try {
      final Reflections reflections = getReflections();
      final ObjectFactory of = new ObjectFactory();
      final BeanConfiguration beanConfiguration = of.createBeanConfiguration();

      // Read converters
      final Set<Class<?>> converterTypes = reflections.getTypesAnnotatedWith( Converts.class );
      for ( final Class<?> converterType : converterTypes ) {
        @SuppressWarnings( "unchecked" )
        final GlobalConverter converter = createGlobalConverter( ( Class<Converter> ) converterType, of );
        beanConfiguration.getConverters().add( converter );
      }

      // Read beans
      final Map<String, Bean> beans = new HashMap<String, Bean>();
      final Set<Class<?>> beanTypes = reflections.getTypesAnnotatedWith( DataSource.class );
      for ( final Class<?> beanType : beanTypes ) {
        findBean( beans, beanType, of );
      }
      // necessary for beans that only inherit fields

      // Read fields and method parameters that are annotated with @Field and @FieldRef
      // 1) Read fields
      // a) @Field and @FieldRef
      final Set<java.lang.reflect.Field> annotatedFields = reflections.getFieldsAnnotatedWith( Field.class );
      annotatedFields.addAll( reflections.getFieldsAnnotatedWith( FieldRef.class ) );
      for ( final java.lang.reflect.Field field : annotatedFields ) {
        final Class<?> c = field.getDeclaringClass();
        if ( AnnotationBeanConfigurationProviderForReflections.logger
                .isLoggable( AnnotationBeanConfigurationProviderForReflections.level ) ) {
          AnnotationBeanConfigurationProviderForReflections.logger.log(
                  AnnotationBeanConfigurationProviderForReflections.level, "scan.process.field",
                  BeanConfiguration.class.getSimpleName(), c.getName(), field.getName() );
        }
        final Bean bean = findBean( beans, c, of );
        final de.ars.daojones.runtime.configuration.beans.Field fieldModel = createField( field, of );
        bean.getFields().add( fieldModel );
      }
      // a) @Id
      final Set<java.lang.reflect.Field> idFields = reflections.getFieldsAnnotatedWith( Id.class );
      for ( final java.lang.reflect.Field field : idFields ) {
        final Class<?> c = field.getDeclaringClass();
        if ( AnnotationBeanConfigurationProviderForReflections.logger
                .isLoggable( AnnotationBeanConfigurationProviderForReflections.level ) ) {
          AnnotationBeanConfigurationProviderForReflections.logger.log(
                  AnnotationBeanConfigurationProviderForReflections.level, "scan.process.idfield",
                  BeanConfiguration.class.getSimpleName(), c.getName(), field.getName() );
        }
        final Bean bean = findBean( beans, c, of );
        bean.setIdField( createIdField( field.getName() ) );
      }
      // 2) Read methods
      final Set<Method> annotatedMethods = reflections.getMethodsAnnotatedWith( Field.class );
      annotatedMethods.addAll( reflections.getMethodsAnnotatedWith( FieldRef.class ) );
      for ( final Method method : annotatedMethods ) {
        final Class<?> c = method.getDeclaringClass();
        if ( AnnotationBeanConfigurationProviderForReflections.logger
                .isLoggable( AnnotationBeanConfigurationProviderForReflections.level ) ) {
          AnnotationBeanConfigurationProviderForReflections.logger.log(
                  AnnotationBeanConfigurationProviderForReflections.level, "scan.process.method",
                  BeanConfiguration.class.getSimpleName(), c.getName(), method.getName(),
                  Arrays.toString( method.getParameterTypes() ) );
        }
        final de.ars.daojones.runtime.configuration.beans.Method methodModel = findMethod( beans, method, of );
        if ( method.getReturnType().equals( Void.TYPE ) ) {
          throw new ConfigurationException(
                  AnnotationBeanConfigurationProviderForReflections.logger.get( "scan.error.method.void",
                          BeanConfiguration.class.getSimpleName(), method.getDeclaringClass().getName(),
                          method.getName(), Arrays.toString( method.getParameterTypes() ) ) );
        }
        methodModel.setResult( createMethodResult( method, of ) );
      }
      // 3) Read parameters
      // after Reading methods e.g. for evaluating methods with both annotated result and parameters
      final Set<String> annotatedParameters = reflections.getStore().get( MethodParametersAnnotationsScanner.class,
              Field.class.getName() );
      annotatedParameters.addAll(
              reflections.getStore().get( MethodParametersAnnotationsScanner.class, FieldRef.class.getName() ) );
      for ( final String descriptor : annotatedParameters ) {
        final Parameter parameter = MethodParametersAnnotationsScanner.getParameterFromDescriptor( descriptor,
                dependenciesClassLoader );
        if ( parameter.isConstructor() ) {
          final Constructor<?> constructor = parameter.getConstructor();
          final int index = parameter.getParameterIndex();
          if ( AnnotationBeanConfigurationProviderForReflections.logger
                  .isLoggable( AnnotationBeanConfigurationProviderForReflections.level ) ) {
            AnnotationBeanConfigurationProviderForReflections.logger.log(
                    AnnotationBeanConfigurationProviderForReflections.level, "scan.process.parameter",
                    BeanConfiguration.class.getSimpleName(), constructor.getDeclaringClass().getName(),
                    AnnotationBeanConfigurationProviderForReflections.logger.get( "constructor.name" ),
                    Arrays.toString( constructor.getParameterTypes() ),
                    NumberFormat.getNumberInstance().format( index ) );
          }
          final de.ars.daojones.runtime.configuration.beans.Constructor constructorModel = findConstructor( beans,
                  constructor, of );
          mapFieldMappedParameter( constructorModel.getParameters().get( index ),
                  constructor.getParameterAnnotations()[index], constructor.getDeclaringClass(), of );
        } else {
          final Method method = parameter.getMethod();
          final int index = parameter.getParameterIndex();
          if ( AnnotationBeanConfigurationProviderForReflections.logger
                  .isLoggable( AnnotationBeanConfigurationProviderForReflections.level ) ) {
            AnnotationBeanConfigurationProviderForReflections.logger.log(
                    AnnotationBeanConfigurationProviderForReflections.level, "scan.process.parameter",
                    BeanConfiguration.class.getSimpleName(), method.getDeclaringClass().getName(), method.getName(),
                    Arrays.toString( method.getParameterTypes() ), NumberFormat.getNumberInstance().format( index ) );
          }
          final de.ars.daojones.runtime.configuration.beans.Method methodModel = findMethod( beans, method, of );
          if ( null != methodModel.getResult() ) {
            throw new ConfigurationException(
                    AnnotationBeanConfigurationProviderForReflections.logger.get( "scan.error.method.annotations",
                            BeanConfiguration.class.getSimpleName(), method.getDeclaringClass().getName(),
                            method.getName(), Arrays.toString( method.getParameterTypes() ) ) );
          }
          mapFieldMappedParameter( methodModel.getParameters().get( index ), method.getParameterAnnotations()[index],
                  method.getDeclaringClass(), of );
        }
      }
      // Validation at the end
      fieldRefIds.removeAll( fieldIds );
      if ( !fieldRefIds.isEmpty() ) {
        throw new ConfigurationException( AnnotationBeanConfigurationProviderForReflections.logger
                .get( "scan.error.unresolved.idrefs", BeanConfiguration.class.getSimpleName(), fieldRefIds ) );
      }
      beanConfiguration.getBeans().addAll( beans.values() );
      return Arrays.asList( beanConfiguration );
    } finally {
      fieldIds.clear();
      fieldRefIds.clear();
      if ( AnnotationBeanConfigurationProviderForReflections.logger
              .isLoggable( AnnotationBeanConfigurationProviderForReflections.level ) ) {
        AnnotationBeanConfigurationProviderForReflections.logger.log(
                AnnotationBeanConfigurationProviderForReflections.level, "scan.finish",
                BeanConfiguration.class.getSimpleName() );
      }
    }
  }

  /**
   * Creates a bean model.
   *
   * @param beanType
   *          the bean type
   * @param of
   *          the model object factory
   * @return the bean model
   */
  protected Bean createBean( final Class<?> beanType, final ObjectFactory of ) {
    final Bean result = of.createBean();
    result.setType( beanType.getName() );
    result.setIdentificator( createBeanIdentificator( beanType.getAnnotation( IdentifiedBy.class ) ) );
    mapDatabaseType( result, beanType.getAnnotation( DataSource.class ), of );
    return result;
  }

  /**
   * Maps a data source to a bean.
   *
   * @param bean
   *          the bean
   * @param dsAnnotation
   *          the data source annotation
   * @param of
   *          the bean model factory
   */
  protected void mapDatabaseType( final Bean bean, final DataSource dsAnnotation, final ObjectFactory of ) {
    if ( null != dsAnnotation ) {
      final DatabaseTypeMapping typeMapping = of.createDatabaseTypeMapping();
      // Default value of value() is "" -> use simple class name
      typeMapping.setName( dsAnnotation.value() );
      // Default value of type() is TABLE
      typeMapping.setType( dsAnnotation.type() );
      bean.setTypeMapping( typeMapping );
    }
  }

  /**
   * Creates the bean identificator.
   *
   * @param annotation
   *          the {@link IdentifiedBy} annotation
   * @return the bean identificator
   */
  protected LocalBeanIdentificator createBeanIdentificator( final IdentifiedBy annotation ) {
    if ( null != annotation && !DefaultBeanIdentificator.class.getName().equals( annotation.value().getName() ) ) {
      final LocalBeanIdentificator result = new LocalBeanIdentificator();
      result.setInstanceProvider( new InstanceByClassProvider<BeanIdentificator>( annotation.value() ) );
      return result;
    } else {
      return null;
    }
  }

  private static String normalize( final String id ) {
    return null == id || id.length() == 0 ? null : id;
  }

  /**
   * Maps a field to a field mapped element.
   *
   * @param model
   *          the field mapped element
   * @param element
   *          the annotated element
   * @param declaringClass
   *          the class that declares the annotated element
   * @param of
   *          the bean model factory
   * @throws ConfigurationException
   */
  protected void mapField( final DatabaseFieldMappedElement model, final AnnotatedElement element,
          final Class<?> declaringClass, final ObjectFactory of ) throws ConfigurationException {
    final Field fieldAnnotation = element.getAnnotation( Field.class );
    if ( null != fieldAnnotation ) {
      final DatabaseFieldMapping fieldMapping = of.createDatabaseFieldMapping();
      fieldMapping.setId( AnnotationBeanConfigurationProviderForReflections.normalize( fieldAnnotation.id() ) );
      fieldMapping.setName( fieldAnnotation.value() );
      fieldMapping.setUpdatePolicy( fieldAnnotation.updatePolicy() );
      final Class<? extends Converter> localConverterClass = fieldAnnotation.converter();
      if ( !DefaultConverter.class.getName().equals( localConverterClass.getName() ) ) {
        fieldMapping.setConverter( createLocalConverter( localConverterClass, of ) );
      }
      model.setFieldMapping( fieldMapping );
      if ( !"".equals( fieldAnnotation.id() ) ) {
        fieldIds.add( AnnotationBeanConfigurationProviderForReflections.logger.get( "field.id.key",
                declaringClass.getName(), fieldAnnotation.id() ) );
      }
      // read out further annotations (not field!)
      final ServiceLoader<FieldAnnotationHandler> handlers = ServiceLoader.load( FieldAnnotationHandler.class,
              dependenciesClassLoader );
      handlers.iterator();
      for ( final FieldAnnotationHandler handler : handlers ) {
        handler.handle( element, model );
      }
      // create interface for annotation handler (annotation, fieldmapping element, model that is mapped)
    }
  }

  /**
   * Maps a field reference to a field mapped element.
   *
   * @param model
   *          the field mapped element
   * @param element
   *          the annotated element
   * @param declaringClass
   *          the class that declares the annotated element
   * @param of
   *          the bean model factory
   */
  protected void mapFieldRef( final DatabaseFieldMappedElement model, final AnnotatedElement element,
          final Class<?> declaringClass, final ObjectFactory of ) {
    final FieldRef fieldAnnotation = element.getAnnotation( FieldRef.class );
    if ( null != fieldAnnotation ) {
      final DatabaseFieldMappingReference fieldMappingRef = of.createDatabaseFieldMappingReference();
      // This is allowed here
      fieldMappingRef.setMapping( fieldAnnotation.value() );
      model.setFieldMappingRef( fieldMappingRef );
      fieldRefIds.add( AnnotationBeanConfigurationProviderForReflections.logger.get( "field.id.key",
              declaringClass.getName(), fieldAnnotation.value() ) );
    }
  }

  /**
   * Creates a field model.
   *
   * @param field
   *          the field
   * @param of
   *          the model object factory
   * @return the field model
   * @throws ConfigurationException
   */
  protected de.ars.daojones.runtime.configuration.beans.Field createField( final java.lang.reflect.Field field,
          final ObjectFactory of ) throws ConfigurationException {
    final Class<?> declaringClass = field.getDeclaringClass();
    final de.ars.daojones.runtime.configuration.beans.Field result = of.createField();
    result.setName( field.getName() );
    mapField( result, field, declaringClass, of );
    mapFieldRef( result, field, declaringClass, of );
    return result;
  }

  /**
   * Creates an id field.
   *
   * @param name
   *          the value of the id field
   * @return the id field
   */
  protected IdField createIdField( final String name ) {
    final IdField result = new IdField();
    result.setName( name );
    return result;
  }

  private MethodResult createMethodResult( final Method method, final ObjectFactory of ) throws ConfigurationException {
    final Class<?> declaringClass = method.getDeclaringClass();
    final MethodResult methodResult = of.createMethodResult();
    mapField( methodResult, method, declaringClass, of );
    mapFieldRef( methodResult, method, declaringClass, of );
    return methodResult;
  }

  protected LocalConverter createLocalConverter( final Class<? extends Converter> converterType,
          final ObjectFactory of ) {
    final LocalConverter result = of.createLocalConverter();
    result.setInstanceProvider( new InstanceByClassProvider<Converter>( converterType ) );
    return result;
  }

  protected GlobalConverter createGlobalConverter( final Class<? extends Converter> converterType,
          final ObjectFactory of ) {
    final GlobalConverter result = of.createGlobalConverter();
    final Converts convertsAnnotation = converterType.getAnnotation( Converts.class );
    result.setInstanceProvider( new InstanceByClassProvider<Converter>( converterType ) );
    result.setConvertType( convertsAnnotation.value().getName() );
    return result;
  }

  private void mapFieldMappedParameter( final MethodParameter parameterModel, final Annotation[] parameterAnnotations,
          final Class<?> declaringClass, final ObjectFactory of ) throws ConfigurationException {
    final AnnotatedElement element = new AnnotatedElement() {

      @Override
      public boolean isAnnotationPresent( final Class<? extends Annotation> annotationClass ) {
        for ( final Annotation annotation : parameterAnnotations ) {
          if ( annotation.annotationType().getName().equals( annotationClass.getName() ) ) {
            return true;
          }
        }
        return false;
      }

      @Override
      public Annotation[] getDeclaredAnnotations() {
        return parameterAnnotations;
      }

      @Override
      public Annotation[] getAnnotations() {
        return parameterAnnotations;
      }

      @SuppressWarnings( "unchecked" )
      @Override
      public <T extends Annotation> T getAnnotation( final Class<T> annotationClass ) {
        for ( final Annotation annotation : parameterAnnotations ) {
          if ( annotation.annotationType().getName().equals( annotationClass.getName() ) ) {
            return ( T ) annotation;
          }
        }
        return null;
      }
    };
    mapField( parameterModel, element, declaringClass, of );
    mapFieldRef( parameterModel, element, declaringClass, of );
    for ( final Annotation annotation : parameterAnnotations ) {
      if ( Field.class.isAssignableFrom( annotation.annotationType() ) ) {
        return;
      } else if ( FieldRef.class.isAssignableFrom( annotation.annotationType() ) ) {
        return;
      }
    }
  }

  private Bean findBean( final Map<String, Bean> beans, final Class<?> c, final ObjectFactory of )
          throws ConfigurationException {
    Bean bean = beans.get( c.getName() );
    if ( null == bean ) {
      bean = createBean( c, of );
      beans.put( c.getName(), bean );
    }
    return bean;
  }

  private de.ars.daojones.runtime.configuration.beans.Constructor createConstructor( final Constructor<?> constructor,
          final ObjectFactory of ) {
    final de.ars.daojones.runtime.configuration.beans.Constructor result = of.createConstructor();
    assignParameterTypes( result.getParameters(), constructor.getParameterTypes(), of );
    return result;
  }

  private de.ars.daojones.runtime.configuration.beans.Method createMethod( final Method method,
          final ObjectFactory of ) {
    final de.ars.daojones.runtime.configuration.beans.Method result = of.createMethod();
    result.setName( method.getName() );
    assignParameterTypes( result.getParameters(), method.getParameterTypes(), of );
    return result;
  }

  private void assignParameterTypes( final List<MethodParameter> resultModel, final Class<?>[] parameterTypes,
          final ObjectFactory of ) {
    for ( final Class<?> parameterType : parameterTypes ) {
      final MethodParameter parameter = createMethodParameter( parameterType, of );
      resultModel.add( parameter );
    }
  }

  private MethodParameter createMethodParameter( final Class<?> parameterType, final ObjectFactory of ) {
    final MethodParameter parameter = of.createMethodParameter();
    parameter.setType( parameterType.getName() );
    return parameter;
  }

  private de.ars.daojones.runtime.configuration.beans.Constructor findConstructor( final Map<String, Bean> beans,
          final Constructor<?> constructor, final ObjectFactory of ) throws ConfigurationException {
    final Class<?> c = constructor.getDeclaringClass();
    final Bean bean = findBean( beans, c, of );
    de.ars.daojones.runtime.configuration.beans.Constructor result = bean.getConstructor();
    if ( null == result ) {
      result = createConstructor( constructor, of );
      bean.setConstructor( result );
    } else {
      // Check parameter types
      final List<MethodParameter> is = result.getParameters();
      final Class<?>[] should = constructor.getParameterTypes();
      if ( !isEqual( is, should ) ) {
        throw new ConfigurationException( AnnotationBeanConfigurationProviderForReflections.logger
                .get( "scan.error.constructor.multiple", BeanConfiguration.class.getSimpleName(), c.getName() ) );
      }
    }
    return result;
  }

  private de.ars.daojones.runtime.configuration.beans.Method findMethod( final Map<String, Bean> beans,
          final Method method, final ObjectFactory of ) throws ConfigurationException {
    final Class<?> c = method.getDeclaringClass();
    if ( Modifier.isStatic( method.getModifiers() ) || Modifier.isAbstract( method.getModifiers() ) ) {
      throw new ConfigurationException( AnnotationBeanConfigurationProviderForReflections.logger.get(
              "scan.error.method.modifiers", BeanConfiguration.class.getSimpleName(), c.getName(), method.getName(),
              Arrays.toString( method.getParameterTypes() ) ) );
    }
    final Bean bean = findBean( beans, c, of );
    // Search for existing method
    for ( final de.ars.daojones.runtime.configuration.beans.Method methodModel : bean.getMethods() ) {
      if ( methodModel.getName().equals( method.getName() ) ) {
        // Check parameter types
        final List<MethodParameter> is = methodModel.getParameters();
        final Class<?>[] should = method.getParameterTypes();
        if ( isEqual( is, should ) ) {
          return methodModel;
        }
      }
    }
    // create new method model
    final de.ars.daojones.runtime.configuration.beans.Method result = createMethod( method, of );
    bean.getMethods().add( result );
    return result;
  }

  private boolean isEqual( final List<MethodParameter> is, final Class<?>[] should ) {
    boolean paramsEqual = is.size() == should.length;
    for ( int i = 0; paramsEqual && i < should.length; i++ ) {
      paramsEqual = paramsEqual && should[i].getName().equals( is.get( i ).getType() );
    }
    return paramsEqual;
  }

}
