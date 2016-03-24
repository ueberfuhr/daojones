package de.ars.daojones.internal.runtime.configuration.context;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.ars.daojones.internal.runtime.configuration.ModelNormalizer;
import de.ars.daojones.internal.runtime.configuration.ServiceLoaderCombinatedNormalizer;
import de.ars.daojones.internal.runtime.configuration.beans.BeanModelNormalizer;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping;
import de.ars.daojones.runtime.configuration.beans.Method;
import de.ars.daojones.runtime.configuration.beans.MethodParameter;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModel.Id;
import de.ars.daojones.runtime.configuration.context.BeanModelImpl;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.ConverterModelManager;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class BeanModelManagerImpl extends ConfigurationModelManagerImpl<BeanModel.Id, BeanModel> implements
        BeanModelManager {

  private static final Messages bundle = Messages.create( "configuration.context.BeanModelManager" );

  /*
   * Bedeutung von packageName:
   *  - Bei Angabe hat die Methode den Default-Scope und überschreibt nur Methoden aus demselben Package.
   *  - Bei Default Scope können nur Default-Scoped-Methoden überschrieben worden sein
   *  - Überschreibende Methoden können public oder protected sein (oder Default Scoped) 
   */
  private static class MethodSignature {
    private final String name;
    private final String[] parameterTypes;
    private final String packageName;

    public MethodSignature( final Method method ) {
      this( method, null );
    }

    public MethodSignature( final Method method, final String packageName ) {
      super();
      name = method.getName();
      final List<MethodParameter> parameters = method.getParameters();
      final List<String> types = new ArrayList<String>( parameters.size() );
      for ( final MethodParameter param : parameters ) {
        types.add( param.getType() );
      }
      parameterTypes = types.toArray( new String[types.size()] );
      this.packageName = packageName;
    }

    public MethodSignature( final MethodSignature signature, final String packageName ) {
      super();
      name = signature.name;
      parameterTypes = signature.parameterTypes;
      this.packageName = packageName;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
      result = prime * result + ( ( packageName == null ) ? 0 : packageName.hashCode() );
      result = prime * result + Arrays.hashCode( parameterTypes );
      return result;
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
      final MethodSignature other = ( MethodSignature ) obj;
      if ( name == null ) {
        if ( other.name != null ) {
          return false;
        }
      } else if ( !name.equals( other.name ) ) {
        return false;
      }
      if ( packageName == null ) {
        if ( other.packageName != null ) {
          return false;
        }
      } else if ( !packageName.equals( other.packageName ) ) {
        return false;
      }
      if ( !Arrays.equals( parameterTypes, other.parameterTypes ) ) {
        return false;
      }
      return true;
    }

  }

  private final ConverterModelManager converterModelManager;

  public BeanModelManagerImpl( final Class<BeanModel> modelClass, final Class<GlobalConverterModel> converterModelClass ) {
    super( modelClass );
    converterModelManager = new ConverterModelManagerImpl( converterModelClass );
  }

  @Override
  public ConverterModelManager getConverterModelManager() {
    return converterModelManager;
  }

  @Override
  public BeanModel getEffectiveModel( final String applicationId, final Class<?> beanType )
          throws ConfigurationException {
    // Constructor: no inheritation -> only invoke constructor of bean type
    // Method: inheritation of methods
    // Field: no inheritation -> collect all fields
    // not only for classes, but for annotated methods of interfaces too
    final Bean bean = new Bean();
    bean.setType( beanType.getName() );

    // Map for overwritten methods
    final Map<MethodSignature, Method> methods = new HashMap<BeanModelManagerImpl.MethodSignature, Method>();
    // copy model for this bean type
    final BeanModel thisModel = getModel( new Id( applicationId, bean.getType() ) );
    boolean modelFound = null != thisModel;
    if ( modelFound ) {
      final Bean thisBean = thisModel.getBean();
      bean.setConstructor( thisBean.getConstructor() );
      bean.setIdentificator( thisBean.getIdentificator() );
      bean.setIdField( thisBean.getIdField() );
      bean.setTypeMapping( thisBean.getTypeMapping() );
      bean.getFields().addAll( thisBean.getFields() );
      for ( final Method method : thisBean.getMethods() ) {
        register( beanType, bean, method, methods );
      }
    }
    // read additional methods and fields from the interfaces and super types
    modelFound = findFieldsAndMethods( applicationId, beanType, bean, methods ) || modelFound;

    if ( modelFound ) {
      // normalize model
      final ModelNormalizer<BeanModel.Id, BeanModel, BeanModelManager> normalizer = new ServiceLoaderCombinatedNormalizer<BeanModel.Id, BeanModel, BeanModelManager, BeanModelNormalizer>(
              BeanModelManagerImpl.class.getClassLoader(), BeanModelNormalizer.class );
      final BeanModel model = new BeanModelImpl( applicationId, bean );
      normalizer.normalize( model, this );

      return model;
    } else {
      return null;
    }

  }

  private boolean findFieldsAndMethods( final String applicationId, final Class<?> beanType, final Bean bean,
          final Map<MethodSignature, Method> methods ) throws ConfigurationException {
    boolean result = false;
    // first, super class is read
    final Class<?> superclass = beanType.getSuperclass();
    if ( null != superclass ) {
      result = findFieldsAndMethodsFromSuperType( applicationId, superclass, bean, methods ) || result;
    }
    // then, annotated methods from interfaces are used
    for ( final Class<?> i : beanType.getInterfaces() ) {
      result = findFieldsAndMethodsFromSuperType( applicationId, i, bean, methods ) || result;
    }
    return result;
  }

  private boolean findFieldsAndMethodsFromSuperType( final String applicationId, final Class<?> beanType,
          final Bean bean, final Map<MethodSignature, Method> methods ) throws ConfigurationException {
    boolean result = false;
    // find model
    final BeanModel model = getModel( new Id( applicationId, beanType.getName() ) );
    if ( null != model ) {
      result = true;
      final Bean modelBean = model.getBean();
      // if model was found
      // do not read constructors - they are always overwritten
      final boolean isInterface = beanType.isInterface();
      if ( !isInterface ) {
        // read id field
        if ( null == bean.getIdField() ) {
          bean.setIdField( modelBean.getIdField() );
        } else if ( null != modelBean.getIdField() ) {
          throw new ConfigurationException(
                  BeanModelManagerImpl.bundle.get( "error.multiple_id_fields", bean.getType() ) );
        }
        // read fields
        // fields cannot be overwritten
        bean.getFields().addAll( modelBean.getFields() );
      }
      final String beanPackage = null == beanType.getPackage() ? "" : beanType.getPackage().getName();
      // read methods and decide whether it is overwritten or not
      for ( final Method method : modelBean.getMethods() ) {
        // find out possibly overwritten method
        final MethodSignature signature = new MethodSignature( method );
        final Method overwritingMethod = methods.get( signature );
        final Method overwritingMethodWithPackage = methods.get( new MethodSignature( signature, beanPackage ) );
        boolean overwritten = null != overwritingMethod || null != overwritingMethodWithPackage;
        // only overwritten, if public (interface), protected or default scoped with the same package
        if ( overwritten && !isInterface ) {
          // check visibility only for classes
          try {
            // get visibility
            final java.lang.reflect.Method reflMethod = BeanModelHelper.findMethod( beanType, method );
            // overwritten, if not private
            overwritten = ( reflMethod.getModifiers() & Modifier.PRIVATE ) == 0x0;
            // overwritten, if not default scoped and overwriting method is declared within another package
            overwritten = overwritten
                    && ( ( reflMethod.getModifiers() & ( Modifier.PROTECTED | Modifier.PUBLIC ) ) != 0x0
                            || null != overwritingMethodWithPackage || BeanModelHelper.getPackage(
                            overwritingMethod.getDeclaringBean() ).equals( beanPackage ) );
          } catch ( final SecurityException e ) {
            throw new ConfigurationException( e );
          } catch ( final ClassNotFoundException e ) {
            throw new ConfigurationException( e );
          } catch ( final NoSuchMethodException e ) {
            throw new ConfigurationException( e );
          }
        }
        if ( !overwritten ) {
          register( beanType, bean, method, methods );
        }
      }

    }
    // step through inheritance hierarchy recursively
    result = findFieldsAndMethods( applicationId, beanType, bean, methods ) || result;
    return result;
  }

  private void register( final Class<?> beanType, final Bean bean, final Method method,
          final Map<MethodSignature, Method> methods ) throws ConfigurationException {
    try {
      // get visibility
      final java.lang.reflect.Method reflMethod = BeanModelHelper.findMethod( beanType, method );
      String packageName = null;
      if ( ( reflMethod.getModifiers() & ( Modifier.PRIVATE | Modifier.PUBLIC | Modifier.PROTECTED ) ) == 0x0 ) {
        packageName = null == beanType.getPackage() ? "" : beanType.getPackage().getName();
      }
      bean.getMethods().add( method );
      methods.put( new MethodSignature( method, packageName ), method );
    } catch ( final SecurityException e ) {
      throw new ConfigurationException( e );
    } catch ( final ClassNotFoundException e ) {
      throw new ConfigurationException( e );
    } catch ( final NoSuchMethodException e ) {
      throw new ConfigurationException( e );
    }
  }

  @Override
  public BeanModel[] findModelsByTypeMapping( final String application, final DatabaseTypeMapping mapping ) {
    final Collection<BeanModel> beanModels = new HashSet<BeanModel>();
    for ( final BeanModel beanModel : getModels() ) {
      if ( application.equals( beanModel.getId().getApplicationId() ) ) {
        final DatabaseTypeMapping resultMapping = beanModel.getBean().getTypeMapping();
        if ( mapping.getName().equals( resultMapping.getName() ) ) {
          if ( mapping.getType().equals( resultMapping.getType() ) ) {
            beanModels.add( beanModel );
          }
        }
      }
    }
    return beanModels.toArray( new BeanModel[beanModels.size()] );
  }

  private static final ModelNormalizer<BeanModel.Id, BeanModel, BeanModelManager> beanModelNormalizer = new ServiceLoaderCombinatedNormalizer<BeanModel.Id, BeanModel, BeanModelManager, BeanModelNormalizer>(
          BeanModelManagerImpl.class.getClassLoader(), BeanModelNormalizer.class );

  @Override
  public void register( final BeanModel model ) throws ConfigurationException {
    // TODO Validate
    BeanModelManagerImpl.beanModelNormalizer.normalize( model, this );
    super.register( model );

  }
}
