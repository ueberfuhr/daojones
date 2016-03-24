package de.ars.daojones.runtime.query;

import de.ars.daojones.annotations.model.ColumnInfo;
import de.ars.daojones.runtime.Dao;

/**
 * A class that resolves names of columns, tables or fields to a query language.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class VariableResolver {

  private final Class<? extends Dao> theGenericClass;

  /**
   * Creates an instance of a resolver.
   * 
   * @param theGenericClass
   *          The class containing the annotations
   */
  public VariableResolver( Class<? extends Dao> theGenericClass ) {
    super();
    this.theGenericClass = theGenericClass;
  }

  /**
   * Returns the class that this resolver is responsible for.
   * 
   * @return the class that this resolver is responsible for
   */
  protected Class<? extends Dao> getTheGenericClass() {
    return this.theGenericClass;
  }

  /**
   * Returns a search query part for the column that can be used to compare with
   * an expression. Implementors have to check the parameter to be null and
   * handle it. If this method returns null, the original fieldname will be used
   * 
   * @param column
   *          the column
   * @throws VariableResolvingException
   *           if resolving failed, e.g. the param was null
   * @return the column names to be searched for
   */
  public abstract String[] resolveColumn( ColumnInfo column )
      throws VariableResolvingException;

}
