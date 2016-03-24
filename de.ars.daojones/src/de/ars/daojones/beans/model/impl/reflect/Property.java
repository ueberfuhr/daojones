package de.ars.daojones.beans.model.impl.reflect;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.Column;
import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.annotations.Transform;
import de.ars.daojones.annotations.model.ColumnInfo;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.IPropertyMethod;
import de.ars.daojones.beans.model.PropertyMethodType;

/**
 * Implementation of {@link IProperty} using Java reflection.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class Property implements IProperty {

  private final IBean parent;
  private final PropertyDescriptor property;

  /**
   * Creates an instance.
   * 
   * @param parent
   *          the parent bean
   * @param property
   *          the {@link PropertyDescriptor}
   */
  public Property( IBean parent, PropertyDescriptor property ) {
    super();
    this.parent = parent;
    this.property = property;
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getBean()
   */
  public IBean getBean() {
    return this.parent;
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getColumn()
   */
  public ColumnInfo getColumn() {
    final Column c = Utilities.getAnnotation( property, Column.class );
    return null != c ? new ColumnInfo( c ) : new ColumnInfo(
        property.getName(), null );
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getGetter()
   */
  public IPropertyMethod getGetter() {
    final Method m = property.getReadMethod();
    return null != m ? new PropertyMethod( this, PropertyMethodType.GETTER, m )
        : null;
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getName()
   */
  public String getName() {
    return property.getName();
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getSetter()
   */
  public IPropertyMethod getSetter() {
    final Method m = property.getWriteMethod();
    return null != m ? new PropertyMethod( this, PropertyMethodType.SETTER, m )
        : null;
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getStrategy()
   */
  public StrategyPolicy getStrategy() {
    final AccessStrategy as = Utilities.getAnnotation( property,
        AccessStrategy.class );
    return null != as ? as.value() : null;
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getTransformer()
   */
  public String getTransformer() {
    final Transform t = Utilities.getAnnotation( property, Transform.class );
    return null != t && null != t.value() ? t.value().getName() : null;
  }

  /**
   * @see de.ars.daojones.beans.model.IProperty#getType()
   */
  public String getType() {
    return Utilities.getTypeName( property.getPropertyType() );
  }

}
