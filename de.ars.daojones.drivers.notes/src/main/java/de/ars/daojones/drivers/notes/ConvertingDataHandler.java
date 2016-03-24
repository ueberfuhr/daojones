package de.ars.daojones.drivers.notes;

import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

/**
 * A data handler that converts the database value after read and before write.
 * This is necessary when the database accessor does not provide a method to
 * directly read the data type.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the data type
 * @param <D>
 *          the database type
 */
public interface ConvertingDataHandler<T, D> {

  /**
   * Converts the field value into the data type.
   * 
   * @param context
   *          the data handler context
   * @param fieldContext
   *          the field context
   * @param value
   *          the field value
   * @return the converted value
   * @throws NotesException
   * @throws DataHandlerException
   */
  T convertAfterRead( DataHandlerContext<?> context, final FieldContext<T> fieldContext, final D value )
          throws NotesException, DataHandlerException;

  /**
   * Converts the data type into the field value.
   * 
   * @param context
   *          the data handler context
   * @param fieldContext
   *          the field context
   * @param value
   *          the value
   * @return the converted field value
   * @throws NotesException
   * @throws DataHandlerException
   */
  D convertForUpdate( final DataHandlerContext<?> context, final FieldContext<T> fieldContext, final T value )
          throws NotesException, DataHandlerException;

}