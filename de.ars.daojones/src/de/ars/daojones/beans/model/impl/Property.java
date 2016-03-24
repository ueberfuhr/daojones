package de.ars.daojones.beans.model.impl;

import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.annotations.model.ColumnInfo;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.IPropertyMethod;

/**
 * Default implementation for {@link IProperty}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class Property implements IProperty {

  private final IBean bean;
  private String name;
  private String type;
  private StrategyPolicy strategy;
  private ColumnInfo column;
  private String transformer;
  private IPropertyMethod getter;
  private IPropertyMethod setter;

  /**
   * Creates an instance.
   * 
   * @param bean
   *          the declaring bean.
   */
  public Property( IBean bean ) {
    super();
    this.bean = bean;
  }

  /**
   * Returns the declaring bean.
   * 
   * @return the declaring bean
   * @see IProperty#getBean()
   */
//TODO Java6-Migration
  //  @Override
  public IBean getBean() {
    return bean;
  }

  /**
   * Returns the name of the property.
   * 
   * @return the name of the property
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the property.
   * 
   * @param name
   *          the name of the property
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Returns the name of the type. This can be a fully qualified class name or a
   * primitive name.
   * 
   * @return the name of the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the name of the type.
   * 
   * @param type
   *          the name of the type
   */
  public void setType( String type ) {
    this.type = type;
  }

  /**
   * Returns the access strategy.
   * 
   * @return the access strategy
   */
  public StrategyPolicy getStrategy() {
    return strategy;
  }

  /**
   * Sets the access strategy.
   * 
   * @param strategy
   *          the access strategy
   */
  public void setStrategy( StrategyPolicy strategy ) {
    this.strategy = strategy;
  }

  /**
   * Returns the column.
   * 
   * @return the column
   */
  public ColumnInfo getColumn() {
    return column;
  }

  /**
   * Sets the column.
   * 
   * @param column
   *          the column
   */
  public void setColumn( ColumnInfo column ) {
    this.column = column;
  }

  /**
   * Returns the transformer.
   * 
   * @return the transformer
   */
  public String getTransformer() {
    return transformer;
  }

  /**
   * Sets the transformer.
   * 
   * @param transformer
   *          the transformer
   */
  public void setTransformer( String transformer ) {
    this.transformer = transformer;
  }

  /**
   * Returns the getter.
   * 
   * @return the getter or null, if the property is not readable
   */
  public IPropertyMethod getGetter() {
    return getter;
  }

  /**
   * Sets the getter.
   * 
   * @param getter
   *          the getter or null, if the property is not readable
   */
  public void setGetter( IPropertyMethod getter ) {
    this.getter = getter;
  }

  /**
   * Returns the setter.
   * 
   * @return the setter or null, if the property is not writable
   */
  public IPropertyMethod getSetter() {
    return setter;
  }

  /**
   * Sets the setter.
   * 
   * @param setter
   *          the setter or null, if the property is not writable
   */
  public void setSetter( IPropertyMethod setter ) {
    this.setter = setter;
  }

  /**
   * @see Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( column == null ) ? 0 : column.hashCode() );
    result = prime * result + ( ( getter == null ) ? 0 : getter.hashCode() );
    result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
    result = prime * result + ( ( setter == null ) ? 0 : setter.hashCode() );
    result = prime * result + ( ( strategy == null ) ? 0 : strategy.hashCode() );
    result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    Property other = ( Property ) obj;
    if ( column == null ) {
      if ( other.column != null )
        return false;
    } else if ( !column.equals( other.column ) )
      return false;
    if ( getter == null ) {
      if ( other.getter != null )
        return false;
    } else if ( !getter.equals( other.getter ) )
      return false;
    if ( name == null ) {
      if ( other.name != null )
        return false;
    } else if ( !name.equals( other.name ) )
      return false;
    if ( setter == null ) {
      if ( other.setter != null )
        return false;
    } else if ( !setter.equals( other.setter ) )
      return false;
    if ( strategy == null ) {
      if ( other.strategy != null )
        return false;
    } else if ( !strategy.equals( other.strategy ) )
      return false;
    if ( type == null ) {
      if ( other.type != null )
        return false;
    } else if ( !type.equals( other.type ) )
      return false;
    return true;
  }

}
