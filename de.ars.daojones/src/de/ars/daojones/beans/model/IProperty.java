package de.ars.daojones.beans.model;

import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.annotations.model.ColumnInfo;

/**
 * A property of a java bean that can be read or written.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IProperty extends IModelElement {

  /**
   * Returns the declaring bean.
   * 
   * @return the declaring bean
   */
  public IBean getBean();

  /**
   * Returns the name of the property.
   * 
   * @return the name of the property
   */
  public String getName();

  /**
   * Returns the name of the type. This can be a fully qualified class name or a
   * primitive name.
   * 
   * @return the name of the type
   */
  public String getType();

  /**
   * Returns the access strategy for this method. If this method does not
   * specify a strategy, the strategy of the bean is used.
   * 
   * @return the access strategy
   */
  public StrategyPolicy getStrategy();

  /**
   * Returns the column. Must not be null.
   * 
   * @return the column
   */
  public ColumnInfo getColumn();

  /**
   * Returns the fully qualified name of the transformer class or null, if not
   * specified.
   * 
   * @return the fully qualified name of the transformer class
   */
  public String getTransformer();

  /**
   * Returns the getter.
   * 
   * @return the getter or null, if the property is not readable
   * @see Modifier
   */
  public IPropertyMethod getGetter();

  /**
   * Returns the setter.
   * 
   * @return the setter or null, if the property is not writable
   * @see Modifier
   */
  public IPropertyMethod getSetter();

}