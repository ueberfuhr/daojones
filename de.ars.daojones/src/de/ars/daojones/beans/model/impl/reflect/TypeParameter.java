package de.ars.daojones.beans.model.impl.reflect;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import de.ars.daojones.beans.model.ITypeParameter;

/**
 * Implementation of {@link ITypeParameter} using Java reflection.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class TypeParameter implements ITypeParameter {

  private final TypeVariable<?> t;

  /**
   * Creates an instance.
   * 
   * @param t
   *          the type variable
   */
  public TypeParameter( TypeVariable<?> t ) {
    super();
    this.t = t;
  }

  /**
   * @see de.ars.daojones.beans.model.ITypeParameter#getName()
   */
  public String getName() {
    return t.getName();
  }

  /**
   * @see de.ars.daojones.beans.model.ITypeParameter#getSupertype()
   */
  @SuppressWarnings( "unchecked" )
  public String getSupertype() {
    final Type[] bounds = t.getBounds();
    String result = Object.class.getName();
    if ( null != bounds && bounds.length > 0 ) {
      final Type type = bounds[0];
      if ( type instanceof Class ) {
        result = ( ( Class ) type ).getName();
      } else if ( type instanceof TypeVariable ) {
        result = ( ( TypeVariable ) type ).getName();
      }
    }
    return result;
  }

}
