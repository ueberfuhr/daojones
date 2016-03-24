package de.ars.daojones.beans.model;

import java.util.Collection;

import de.ars.daojones.annotations.Abstract;
import de.ars.daojones.annotations.StrategyPolicy;

/**
 * A DaoJones DAO Bean.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IBean extends IModelElement, IModifierContainer {

  /**
   * Returns the package name.
   * 
   * @return the package name
   */
  public abstract String getPackageName();

  /**
   * Returns the unqualified name.
   * 
   * @return the unqualified name
   */
  public abstract String getName();

  /**
   * Returns the collection of type parameters. If the type does not have any
   * type parameter, the result may be empty or null.
   * 
   * @return the collection of type parameters
   */
  public abstract Collection<ITypeParameter> getTypeParameters();

  /**
   * Returns the constructors that have to be re-defined. If the default
   * constructor is valid, this method may return the default constructor, an
   * empty collection or null.
   * 
   * @return the constructors
   */
  public abstract Collection<IConstructor> getConstructors();

  /**
   * Returns the properties.
   * 
   * @return the properties
   */
  public abstract Collection<IProperty> getProperties();

  /**
   * Returns a flag indicating whether the bean class is an interface or not.
   * 
   * @return true, if the class is an interface.
   */
  public abstract boolean isInterface();

  /**
   * Returns true if the bean has the {@link Abstract} annotation.
   * 
   * @return true if the bean has the {@link Abstract} annotation
   */
  public abstract boolean isAbstract();

  /**
   * Returns the default access strategy for all methods that do not explicitly
   * define a custom one.
   * 
   * @return the access strategy
   */
  public StrategyPolicy getStrategy();

  /**
   * Returns true if a given method is implemented by the bean. In this case,
   * the super method must be called from the generated code.
   * 
   * @param name
   *          the name of the method
   * @param parameterTypes
   *          the names of the parameter types
   * 
   * @return true if the method is implemented, otherwise false
   */
  public boolean isMethodImplemented( String name, String... parameterTypes );

}