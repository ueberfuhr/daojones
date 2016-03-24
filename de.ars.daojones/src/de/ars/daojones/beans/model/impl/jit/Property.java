package de.ars.daojones.beans.model.impl.jit;

import javassist.CtClass;
import javassist.CtMethod;
import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.annotations.model.ColumnInfo;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.IPropertyMethod;
import de.ars.daojones.beans.model.PropertyMethodType;

/**
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
public class Property implements IProperty {

  private final IBean parent;
  private final IProperty delegate;

  /**
   * Creates an instance.
   * 
   * @param parent
   *          the parent {@link IBean}
   * @param delegate
   *          the delegate {@link IProperty}
   */
  public Property( IBean parent, IProperty delegate ) {
    super();
    this.parent = parent;
    this.delegate = delegate;
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getBean()
   */
  public IBean getBean() {
    return parent;
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getColumn()
   */
  public ColumnInfo getColumn() {
    return delegate.getColumn();
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getName()
   */
  public String getName() {
    return delegate.getName();
  }

  private CtMethod findMethod( CtClass c, IPropertyMethod method ) {
    final String propertyType = getType();
    final boolean isBooleanProperty = Boolean.TYPE.getName().equals(
        propertyType );
    final boolean isSetter = PropertyMethodType.SETTER == method.getType();
    final String propertyFirstUpper = getName().substring( 0, 1 ).toUpperCase()
        + ( getName().length() > 1 ? getName().substring( 1 ) : "" );
    final String methodName = (isSetter ? "set" : ( isBooleanProperty ? "is"
        : "get" ))
        + propertyFirstUpper;
    final String[] params = isSetter ? new String[] { propertyType }
        : new String[0];
    return Utilities.findMethod( c, methodName, params );
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getGetter()
   */
  public IPropertyMethod getGetter() {
    final IPropertyMethod delegateMethod = delegate.getGetter();
    if ( null != delegateMethod ) {
      return new PropertyMethod( this, findMethod( ( ( Bean ) getBean() )
          .getCtClass(), delegateMethod ), PropertyMethodType.GETTER );
    } else {
      return null;
    }
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getSetter()
   */
  public IPropertyMethod getSetter() {
    final IPropertyMethod delegateMethod = delegate.getSetter();
    if ( null != delegateMethod ) {
      return new PropertyMethod( this, findMethod( ( ( Bean ) getBean() )
          .getCtClass(), delegateMethod ), PropertyMethodType.SETTER );
    } else {
      return null;
    }
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getStrategy()
   */
  public StrategyPolicy getStrategy() {
    return delegate.getStrategy();
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getTransformer()
   */
  public String getTransformer() {
    return delegate.getTransformer();
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getType()
   */
  public String getType() {
    return delegate.getType();
  }

}
