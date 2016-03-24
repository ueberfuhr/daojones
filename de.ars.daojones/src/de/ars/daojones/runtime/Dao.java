package de.ars.daojones.runtime;

import java.io.Serializable;

import de.ars.daojones.FieldAccessor;
import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.runtime.internal.OverrideAllowed;

/**
 * A data access object for accessing the database. This interface is the basic
 * interface for DaoJones DAOs.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@AccessStrategy( StrategyPolicy.LAZY )
public interface Dao extends Serializable {

  /**
   * Returns an object containing information that identifies the object in the
   * database. This object is used for UPDATE or DELETE statements and is
   * created when reading or updating a DAO for the first time. <br/>
   * <b>This method is not intended to be implemented by clients.</b>
   * 
   * @return the object containing information that identifies the object in the
   *         database
   */
  public DataObject getDataObject();

  /**
   * Sets the object containing information that identifies the object in the
   * database. This method is called by the framework when inserting or updating
   * objects. When field values were already set (case of creation), the changes
   * are committed to the {@link DataObject}'s {@link FieldAccessor}. <br/>
   * <b>This method is not intended to be implemented by clients.</b>
   * 
   * @param o
   *          the object containing information that identifies the object in
   *          the database
   * @throws DataAccessException
   *           if setting the field updates to the databasem failed
   */
  public void setDataObject( DataObject o ) throws DataAccessException;

  /**
   * Checks if the object does not yet exist in the database. The method should
   * check if the object has a DataObject, but may be overwritten to add further
   * tests. <br/>
   * <b>This method is not intended to be implemented by clients.</b>
   * 
   * @return true if the object does not yet exist in the database
   */
  public boolean isNew();

  /**
   * This method is called after the object is created in the database.
   * Implement this method for event handling issues.
   * 
   * @throws DataAccessException
   */
  @OverrideAllowed
  public void onCreate() throws DataAccessException;

  /**
   * This method is called before the object is updates into the database. Use
   * this to write other objects to the database too. Implement this method for
   * event handling issues.
   * 
   * @throws DataAccessException
   *           if storing the objects fails
   */
  @OverrideAllowed
  public void onUpdate() throws DataAccessException;

  /**
   * This method is called before the object is deleted from the database. Use
   * this to delete referenced objects from the database too. Implement this
   * method for event handling issues.
   * 
   * @throws DataAccessException
   *           if storing the objects fails
   */
  @OverrideAllowed
  public void onDelete() throws DataAccessException;

  /**
   * Returns the ID of the {@link ApplicationContext}. <br/>
   * <b>This method is not intended to be implemented by clients.</b>
   * 
   * @return the ID of the {@link ApplicationContext}
   */
  public String getApplicationContextId();

  /**
   * Sets the id of the application context. This is called by the framework
   * when creating the object. <br/>
   * <b>This method is not intended to be implemented by clients.</b>
   * 
   * @param applicationContextId
   *          the ID of the {@link ApplicationContext}
   */
  public void setApplicationContextId( String applicationContextId );

  /**
   * Returns the field accessor for reading properties from the
   * {@link DataObject}. If the {@link Dao} is new, it does not have any
   * {@link DataObject}, so this method will return a command-pattern-designed
   * temporary {@link FieldAccessor}. <br/>
   * <b>This method is not intended to be implemented by clients.</b>
   * 
   * @return the field accessor
   */
  public FieldAccessor getFieldAccessor();

  /**
   * Refreshes the field values of this bean. Call this when all cached field
   * values should be cleaned.
   * 
   * @throws DataAccessException
   *           if refreshing the field values fails
   */
  @OverrideAllowed
  public void refresh() throws DataAccessException;

  /*
   * Re-declare object methods to annotate them.
   */

  /**
   * @see Object#equals(Object)
   */
  @OverrideAllowed
  public boolean equals( Object o );

  /**
   * @see Object#hashCode()
   */
  @OverrideAllowed
  public int hashCode();

  /**
   * @see Object#toString()
   */
  @OverrideAllowed
  public String toString();
}
