package de.ars.daojones.beans.model.ast;

import static de.ars.daojones.beans.model.ast.Utilities.toModifiers;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getAnnotation;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getAnnotationValue;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getEnumerationValue;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.hasModifier;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isGetter;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isObjectType;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isSetter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;

import de.ars.daojones.annotations.Abstract;
import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.annotations.model.ColumnInfo;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IConstructor;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.IPropertyMethod;
import de.ars.daojones.beans.model.ITypeParameter;
import de.ars.daojones.beans.model.PropertyMethodType;

/**
 * Implementation of {@link IBean} interface based on {@link IBinding}s.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class ASTBeanImpl extends AbstractModelElement<ITypeBinding>
    implements IBean {

  /**
   * Creates an instance based on a {@link ITypeBinding}.
   * 
   * @param binding
   *          the binding
   */
  public ASTBeanImpl( final ITypeBinding binding ) {
    super( binding );
  }

  /**
   * Returns the name of the inheritations file. The file must exist at the
   * daojones.generated source folder.
   * 
   * @return the name of the inheritations file
   */
  public abstract String getInheritationsFile();

  /*
   * *********************************************
   *   I B E A N   I M P L E M E N T A T I O N 
   * *********************************************
   */

  /**
   * @see de.ars.daojones.beans.model.IBean#getModifiers()
   */
  @Override
  public Set<de.ars.daojones.beans.model.Modifier> getModifiers() {
    final Set<de.ars.daojones.beans.model.Modifier> result = toModifiers( getBinding()
        .getModifiers() );
    result.remove( de.ars.daojones.beans.model.Modifier.ABSTRACT );
    result.add( isAbstract() ? de.ars.daojones.beans.model.Modifier.ABSTRACT
        : de.ars.daojones.beans.model.Modifier.FINAL );
    return result;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getName()
   */
  @Override
  public String getName() {
    return getBinding().getName();
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getPackageName()
   */
  @Override
  public String getPackageName() {
    return getBinding().getPackage().getName();
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#isAbstract()
   */
  @Override
  public boolean isAbstract() {
    return null != getAnnotation( getBinding(), Abstract.class );
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#isInterface()
   */
  @Override
  public boolean isInterface() {
    return getBinding().isInterface();
  }

  private static final IProperty merge( final IProperty p1,
      final ASTPropertyImpl p2 ) {
    if ( null == p1 )
      return p2;
    if ( null == p2 )
      return p1;
    return new MergedProperty( p1, p2 );
  }

  /**
   * A class merging two properties. Both can be {@link MergedProperty}
   * instances too. Each one can contain getter or setter.
   * 
   * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
   */
  private static class MergedProperty extends AbstractModelElement<IProperty>
      implements IProperty {
    private final ASTPropertyImpl p2;

    public MergedProperty( IProperty original, ASTPropertyImpl p2 ) {
      super( original );
      this.p2 = p2;
    }

    @Override
    public String getName() {
      return p2.getName();
    }

    @Override
    public String getType() {
      return p2.getType();
    }

    @Override
    public ColumnInfo getColumn() {
      final ColumnInfo p2Column = p2.getColumn();
      return null != p2Column ? p2Column : getBinding().getColumn();
    }

    private class MergedPropertyMethod implements IPropertyMethod {
      private final IPropertyMethod delegate;

      public MergedPropertyMethod( IPropertyMethod delegate ) {
        super();
        this.delegate = delegate;
      }

      @Override
      public IProperty getProperty() {
        return MergedProperty.this;
      }

      @Override
      public PropertyMethodType getType() {
        return delegate.getType();
      }

      @Override
      public Set<de.ars.daojones.beans.model.Modifier> getModifiers() {
        return delegate.getModifiers();
      }

      @Override
      public List<String> getExceptionTypes() {
        return delegate.getExceptionTypes();
      }
    }

    @Override
    public IPropertyMethod getGetter() {
      final IPropertyMethod p2Getter = p2.getGetter();
      final IPropertyMethod result = null != p2Getter ? ( isAbstract( p2 ) ? p2Getter
          : null )
          : getBinding().getGetter();
      return null != result ? new MergedPropertyMethod( result ) : null;
    }

    @Override
    public IPropertyMethod getSetter() {
      final IPropertyMethod p2Setter = p2.getSetter();
      final IPropertyMethod result = null != p2Setter ? ( isAbstract( p2 ) ? p2Setter
          : null )
          : getBinding().getSetter();
      return null != result ? new MergedPropertyMethod( result ) : null;
    }

    @Override
    public StrategyPolicy getStrategy() {
      final StrategyPolicy strategy2 = p2.getStrategy();
      return null != strategy2 ? strategy2 : getBinding().getStrategy();
    }

    @Override
    public IBean getBean() {
      return getBinding().getBean();
    }

    @Override
    public String toString() {
      return super.toString() + " + " + p2;
    }

    private static boolean isAbstract( AbstractModelElement<IMethodBinding> p ) {
      return ( p.getBinding().getModifiers() & Modifier.ABSTRACT ) > 0;
    }

    @Override
    public String getTransformer() {
      final String transformer2 = p2.getTransformer();
      return null != transformer2 ? transformer2 : getBinding()
          .getTransformer();
    }
  }

  private final Map<String, IProperty> findProperties( ITypeBinding binding ) {
    final Map<String, IProperty> propsByName = new HashMap<String, IProperty>();
    final ITypeBinding superType = binding.getSuperclass();
    if ( !isObjectType( superType ) ) {
      // final Map<String, IProperty> subclassProperties =
      // findProperties(superType);
      // for(Map.Entry<String, IProperty> entry :
      // subclassProperties.entrySet()) {
      // propsByName.put(entry.getKey(),
      // merge(propsByName.get(entry.getKey()), entry.getValue()));
      // }
      propsByName.putAll( findProperties( superType ) );
    }
    for ( IMethodBinding method : binding.getDeclaredMethods() ) {
      if ( hasModifier( method, Modifier.ABSTRACT )
          && ( isGetter( method ) || isSetter( method ) ) ) {
        final ASTPropertyImpl p = new ASTPropertyImpl( this, method );
        propsByName
            .put( p.getName(), merge( propsByName.get( p.getName() ), p ) );
      }
    }
    return propsByName;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getProperties()
   */
  @Override
  public Collection<IProperty> getProperties() {
    if ( isAbstract() )
      return null;
    final Map<String, IProperty> propsByName = findProperties( getBinding() );
    for ( Iterator<Map.Entry<String, IProperty>> it = propsByName.entrySet()
        .iterator(); it.hasNext(); ) {
      final Map.Entry<String, IProperty> entry = it.next();
      if ( null == entry.getValue().getColumn() )
        it.remove();
    }
    return propsByName.values();
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getConstructors()
   */
  @Override
  public Collection<IConstructor> getConstructors() {
    final Collection<IConstructor> result = new LinkedList<IConstructor>();
    for ( IMethodBinding m : this.getBinding().getDeclaredMethods() ) {
      if ( m.isConstructor() ) {
        result.add( new ASTConstructorImpl( this, m ) );
      }
    }
    return result;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getTypeParameters()
   */
  @Override
  public Collection<ITypeParameter> getTypeParameters() {
    final Collection<ITypeParameter> result = new LinkedList<ITypeParameter>();
    for ( ITypeBinding binding : this.getBinding().getTypeParameters() ) {
      result.add( new ASTTypeParameter( binding ) );
    }
    return result;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#isMethodImplemented(java.lang.String,
   *      java.lang.String[])
   */
  @Override
  public boolean isMethodImplemented( String name, String... parameterTypes ) {
    return Utilities.isMethodImplemented( getBinding(), name, parameterTypes );
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getStrategy()
   */
  @Override
  public StrategyPolicy getStrategy() {
    final IAnnotationBinding annotation = getAnnotation( getBinding(),
        AccessStrategy.class );
    final Object value = getAnnotationValue( annotation );
    return getEnumerationValue( StrategyPolicy.class, value );
  }

}
