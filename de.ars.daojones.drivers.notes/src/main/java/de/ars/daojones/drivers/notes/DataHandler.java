package de.ars.daojones.drivers.notes;

import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.ViewEntry;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

/**
 * A data handler is responsible for reading and writing a given type of data
 * into the database.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the type that is read or written
 */
public interface DataHandler<T> extends Aware<T> {

  /**
   * Reads the field value from a document.
   * 
   * @param context
   *          the data handler context
   * @param fieldContext
   *          the field context
   * @return the field value
   * @throws NotesException
   * @throws DataHandlerException
   */
  T readDocument( DataHandlerContext<Document> context, FieldContext<T> fieldContext ) throws NotesException,
          DataHandlerException;

  /**
   * Reads the field value from a view entry.
   * 
   * @param context
   *          the data handler context
   * @param fieldContext
   *          the field context
   * @return the field value
   * @throws NotesException
   * @throws DataHandlerException
   */
  T readView( DataHandlerContext<ViewEntry> context, FieldContext<T> fieldContext ) throws NotesException,
          DataHandlerException;

  /**
   * Writes a field value into a document.
   * 
   * @param context
   *          the data handler context
   * @param fieldContext
   *          the field context
   * @param value
   *          the field value
   * @throws NotesException
   * @throws DataHandlerException
   */
  void writeDocument( DataHandlerContext<Document> context, FieldContext<T> fieldContext, T value )
          throws NotesException, DataHandlerException;

  /**
   * Writes a field value into a view entry.
   * 
   * @param context
   *          the data handler context
   * @param fieldContext
   *          the field context
   * @param value
   *          the field value
   * @throws NotesException
   * @throws DataHandlerException
   */
  void writeView( DataHandlerContext<ViewEntry> context, FieldContext<T> fieldContext, T value ) throws NotesException,
          DataHandlerException;

  /**
   * The context of a data handling operation.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <Source>
   *          the type of Notes source that is used
   */
  interface DataHandlerContext<Source extends Base> {
    /**
     * Returns the application id.
     * 
     * @return the application id
     */
    String getApplication();

    /**
     * Returns the Notes source.
     * 
     * @return the Notes source
     * @throws NotesException
     */
    Source getSource() throws NotesException;

    /**
     * Returns the Notes session.
     * 
     * @return the Notes session
     * @throws NotesException
     */
    Session getSession() throws NotesException;

    /**
     * Returns the current database.
     * 
     * @return the current database
     * @throws NotesException
     */
    Database getDatabase() throws NotesException;

    /**
     * Returns the data handler provider.
     * 
     * @return the data handler provider
     */
    DataHandlerProvider getDataHandlerProvider();
  }

}
