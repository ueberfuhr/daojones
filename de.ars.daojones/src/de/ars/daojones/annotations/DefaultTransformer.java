package de.ars.daojones.annotations;

import de.ars.daojones.FieldAccessException;
import de.ars.daojones.runtime.Dao;

/**
 * A default implementation of {@link Transformer} that does not transform the
 * value.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 *@since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class DefaultTransformer implements Transformer {

  private static final long serialVersionUID = 1L;

  private final Class<?> c;

  /**
   * Creates an instance.
   * 
   * @param c
   *          the base class
   */
  public DefaultTransformer( Class<?> c ) {
    super();
    this.c = c;
  }

  /**
   * @see de.ars.daojones.annotations.Transformer#getDatabaseType()
   */
  public Class<?> getDatabaseType() {
    return c;
  }

  /**
   * @see de.ars.daojones.annotations.Transformer#getFieldValue(Object, String,
   *      Dao)
   */
  public Object getFieldValue( Object databaseValue, String fieldName, Dao dao )
      throws FieldAccessException {
    return databaseValue;
  }

  /**
   * @see de.ars.daojones.annotations.Transformer#setFieldValue(Object, String,
   *      Dao, boolean)
   */
  public Object setFieldValue( Object javaValue, String fieldName, Dao dao,
      boolean commit ) throws FieldAccessException {
    return javaValue;
  }

}
