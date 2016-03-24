package de.ars.daojones.annotations;

import java.io.Serializable;

import de.ars.daojones.FieldAccessException;
import de.ars.daojones.FieldAccessor;
import de.ars.daojones.runtime.Dao;

/**
 * A common interface for a transformer that transforms an unspecified type into
 * a type that a {@link FieldAccessor} supports.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface Transformer extends Serializable {

  /**
   * Returns the type that a {@link FieldAccessor} can handle.
   * 
   * @return the type that a {@link FieldAccessor} can handle
   */
  public Class<?> getDatabaseType();

  /**
   * Transforms the database type into a Java type. This method is called for
   * reading a database field.
   * 
   * @param databaseValue
   *          the database value
   * @param fieldName
   *          the database field name
   * @param dao
   *          the dao
   * @return the Java value
   * @throws FieldAccessException
   */
  public Object getFieldValue( Object databaseValue, String fieldName, Dao dao )
      throws FieldAccessException;

  /**
   * Transforms the Java type into the database type. This method is called for
   * writing a database field.
   * 
   * @param javaValue
   *          the Java value
   * @param fieldName
   *          the database field name
   * @param dao
   *          the dao
   * @param commit
   *          a flag indicating whether the value is saved immediately after
   *          transformation
   * @return the database value
   * @throws FieldAccessException
   */
  public Object setFieldValue( Object javaValue, String fieldName, Dao dao,
      boolean commit ) throws FieldAccessException;

}
